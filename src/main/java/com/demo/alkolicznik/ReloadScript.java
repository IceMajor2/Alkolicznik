package com.demo.alkolicznik;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.Utils;
import io.imagekit.sdk.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Launching this class reloads database both from
 * this web application, and from remote ImageKit server.
 */
@Configuration
@ConditionalOnProperty(
        prefix = "reload",
        value = "data",
        havingValue = "true",
        matchIfMissing = false
)
@RequiredArgsConstructor
@PropertySource("classpath:imageKit.properties")
@Slf4j
public class ReloadScript implements CommandLineRunner {

    private final ImageKitRepository imageKitRepository;
    private final BeerRepository beerRepository;
    private final StoreRepository storeRepository;

    private final BeerImageService beerImageService;
    private final StoreImageService storeImageService;

    @Value("${imageKit.path}")
    private String imageKitPath;
    private final String RELATIVE_TO_BEER = "/src" + imageKitPath + "/resources/images/beer";
    private final String RELATIVE_TO_STORE = "/src" + imageKitPath + "/resources/images/store";

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        log.info("Executing SQL scripts in resources folder...");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/delete.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("/schema.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("/data.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Reloading ImageKit directory");
        log.info("Deleting remote directory: '%s'...".formatted(imageKitPath));
        deleteFolder("");
        log.info("Reloading BEER images...");
        sendAll("images/beer", BeerImage.class);
        log.info("Sending STORE images to remote...");
        sendAll("images/store", StoreImage.class);
        log.info("Successfully reloaded ImageKit directory");
    }

    private <T extends ImageModel> void sendAll(String srcPath, Class<T> imgClass) throws IOException {
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
