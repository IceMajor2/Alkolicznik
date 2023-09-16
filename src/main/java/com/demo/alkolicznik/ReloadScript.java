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
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;

/**
 * Launching this class reloads database both from
 * this web application, and from remote ImageKit server.
 */
@Component
@ConditionalOnProperty(
        prefix = "command.line.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@RequiredArgsConstructor
@PropertySource("classpath:imageKit.properties")
public class ReloadScript implements CommandLineRunner {

    public static void main(String[] args) {
        turnOn = true;
        SpringApplication.run(AlkolicznikApplication.class, args);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadScript.class);
    private static boolean turnOn = false;

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
    @ConditionalOnClass(ReloadScript.class)
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        if (turnOn) {
            LOGGER.info("Executing SQL scripts in resources folder...");
            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
            resourceDatabasePopulator.addScript(new ClassPathResource("/delete.sql"));
            resourceDatabasePopulator.addScript(new ClassPathResource("/schema.sql"));
            resourceDatabasePopulator.addScript(new ClassPathResource("/data.sql"));

            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
            return dataSourceInitializer;
        }
        return null;
    }

    @Override
    public void run(String... args) throws Exception {
        if (turnOn) {
            LOGGER.info("Reloading ImageKit directory");
            LOGGER.info("Deleting remote directory: '%s'...".formatted(imageKitPath));
            deleteFolder("");
            LOGGER.info("Reloading BEER images...");
            sendAll("/images/beer", BeerImage.class);
            LOGGER.info("Sending STORE images to remote...");
            sendAll("/images/store", StoreImage.class);
            LOGGER.info("Successfully reloaded ImageKit directory");
        }
    }

    @SneakyThrows
    private <T extends ImageModel> void sendAll(String srcPath, Class<T> imgClass) {
        File[] imageDirectory = new File(new ClassPathResource(srcPath).getURI().getRawPath()).listFiles();
        for (File image : imageDirectory) {
            String absolutePath = image.getAbsolutePath();
            String srcFilename = image.getName();
            Object model = null;
            try {
                model = getAssociatedModel(srcFilename, imgClass);
            } catch (BeerNotFoundException e) {
                LOGGER.warn("Beer image ('{}/{}') was not loaded. Failed to determine beer ID from filename: " +
                                "ID needs to be specified as first characters in the filename",
                        RELATIVE_TO_BEER, srcFilename);
            } catch (BeerAlreadyExistsException e) {
                LOGGER.warn("You have duplicated IDs in '{}'. Image '{}' was not initialized",
                        RELATIVE_TO_BEER, srcFilename);
            } catch (StoreNotFoundException e) {
                LOGGER.warn("Store image ('{}/{}') was not loaded. Failed to determine store name from filename: " +
                                "filename should only consist of exact store name (case insensitive). " +
                                "Moreover, store should be included in database",
                        RELATIVE_TO_STORE, srcFilename);
            }
            if (model != null)
                LOGGER.info("Image was successfully sent: {}", addImage(model, absolutePath));
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
