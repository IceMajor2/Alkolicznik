package com.demo.alkolicznik.datascripts.workers;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.datascripts.conditions.ConditionalOnDataScript;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.Utils;
import io.imagekit.sdk.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
@Conditional(ConditionalOnDataScript.class)
@RequiredArgsConstructor
@Slf4j
public class ImageKitReloader {

    private final ImageKitRepository imageKitRepository;
    private final BeerRepository beerRepository;
    private final StoreRepository storeRepository;

    private final BeerImageService beerImageService;
    private final StoreImageService storeImageService;

//    public void reload(String imageKitPath) throws IOException {
//        log.info("Deleting remote directory: '%s'...".formatted(imageKitPath));
//        deleteFolder("");
//        log.info("Reloading BEER images...");
//        sendAll("images/beer", imageKitPath, BeerImage.class);
//        log.info("Sending STORE images to remote...");
//        sendAll("images/store", imageKitPath, StoreImage.class);
//    }

    public void delete(String imageKitPath) {
        log.info("Deleting ImageKit directory: '%s'...".formatted(imageKitPath));
        deleteFolder("");
    }

    public <T extends ImageModel> void upload(String imageKitPath, String srcDir, Class<T> imgClass) throws IOException {
        sendAll(srcDir, imageKitPath, imgClass);
    }

    private <T extends ImageModel> void sendAll(String srcPath, String remotePath, Class<T> imgClass) throws IOException {
        final String RELATIVE_TO_BEER = "/src" + remotePath + "/resources/images/beer";
        final String RELATIVE_TO_STORE = "/src" + remotePath + "/resources/images/store";

        ResourcePatternResolver resourcePatResolver = new PathMatchingResourcePatternResolver();
        Resource[] directory = resourcePatResolver.getResources("classpath:" + srcPath + "/*");

        for (Resource file : directory) {
            String srcFilename = FilenameUtils.getName(file.getURI().toString());
            InputStream inputStream = file.getInputStream();
            File tempFile = Utils.createTempFile(srcFilename, inputStream);
            String absolutePath = tempFile.getAbsolutePath();

            Object model = null;
            try {
                model = getAssociatedModel(srcFilename, imgClass);
            } catch (BeerNotFoundException e) {
                log.warn("Beer image ('{}/{}') was not loaded. Possible reasons:\n1) Failed to determine beer ID " +
                                "from filename: ID needs to be specified as first characters in the filename\n" +
                                "2) There is no beer of specified id",
                        RELATIVE_TO_BEER, srcFilename);
            } catch (BeerAlreadyExistsException e) {
                log.warn("You have duplicated IDs in '{}'. Image '{}' was not initialized",
                        RELATIVE_TO_BEER, srcFilename);
            } catch (StoreNotFoundException e) {
                log.warn("Store image ('{}/{}') was not loaded. Possible reasons:\n1) Failed to determine store name " +
                                "from filename: filename should only consist of exact store name (case insensitive)\n" +
                                "2) There is no store of specified name",
                        RELATIVE_TO_STORE, srcFilename);
            }
            if (model != null)
                addImage(model, absolutePath);
        }
    }

    private Object addImage(Object model, String absolutePath) {
        if (model instanceof Beer beer)
            return beerImageService.add(beer.getId(), new ImageRequestDTO(absolutePath));
        else if (model instanceof Store store)
            return storeImageService.add(store.getName(), new ImageRequestDTO(absolutePath));
        throw new RuntimeException("Class is not supported");
    }

    private Object getAssociatedModel(String filename, Class<? extends ImageModel> imgClass) {
        if (Utils.isBeerImage(imgClass)) {
            Long beerId = Utils.getBeerIdFromFilename(filename);
            if (beerId == null) throw new BeerNotFoundException("null");
            Beer beer = beerRepository.findById(beerId)
                    .orElseThrow(() -> new BeerNotFoundException(beerId));
            return beer;
        } else if (Utils.isStoreImage(imgClass)) {
            String rawStoreName = Utils.getRawStoreNameFromFilename(filename);
            Store store = storeRepository.findByNameIgnoreCase(rawStoreName).stream().findFirst()
                    .orElseThrow(() -> new StoreNotFoundException(rawStoreName, true));
            return store;
        }
        throw new RuntimeException("Class is not supported");
    }

    private void deleteFolder(String path) {
        try {
            imageKitRepository.deleteFolder(path);
        } catch (NotFoundException e) {}
    }
}
