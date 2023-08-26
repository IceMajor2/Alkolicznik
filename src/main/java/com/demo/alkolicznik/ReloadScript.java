package com.demo.alkolicznik;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreImageRepository;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.models.BaseFile;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.repositories.ImageKitRepository.getUpdatedAt;

/**
 * Launching this class reloads everything initial data
 * both from this web application, and remote ImageKit server.
 */
@Component
@ConditionalOnProperty(
        prefix = "command.line.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@AllArgsConstructor
public class ReloadScript implements CommandLineRunner {

    public static void main(String[] args) {
        turnOn = true;
        SpringApplication.run(AlkolicznikApplication.class, args);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadScript.class);

    private static boolean turnOn = false;

    private ImageKitRepository imageKitRepository;

    private BeerImageRepository beerImageRepository;

    private StoreImageRepository storeImageRepository;

    @Override
    public void run(String... args) throws Exception {
        if (turnOn) {
            LOGGER.info("Reloading ImageKit directory");
            LOGGER.info("Deleting remote directory '%s'...".formatted("/main"));
            deleteFolder("");
            LOGGER.info("Sending BEER images to remote...");
            sendImages("/images/beer", "/beer", BeerImage.class);
            LOGGER.info("Sending STORE images to remote...");
            sendImages("/images/store", "/store", StoreImage.class);
            LOGGER.info("Updating image tables with remote IDs...");
            updateBeerImageWithRemoteIDs();
            updateStoreImageWithRemoteIDs();
            LOGGER.info("Updating 'store_image' URLs with 'updatedAt' key...");
            updateUrlWithUpdatedAt(storeImageRepository);
            LOGGER.info("Updating 'beer_image' URLs with 'updatedAt' key...");
            updateUrlWithUpdatedAt(beerImageRepository);
            LOGGER.info("Successfully reloaded ImageKit directory");
        }
    }

    @Bean
    @ConditionalOnClass(ReloadScript.class)
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        if (turnOn) {
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

    private void deleteFolder(String path) {
        try {
            imageKitRepository.deleteFolder(path);
        } catch (NotFoundException e) {
        }
    }

    @SneakyThrows
    private void sendImages(String srcPath, String remoteDir, Class<? extends ImageModel> imgClass) {
        File[] imageDirectory = new File(new ClassPathResource(srcPath).getURI().getRawPath()).listFiles();
        for (File image : imageDirectory) {
            imageKitRepository.save(image.getAbsolutePath(), remoteDir, image.getName(), imgClass);
        }
    }

    private void updateBeerImageWithRemoteIDs() {
        List<BaseFile> externalFiles = imageKitRepository.findAllIn("/beer");
        Map<BaseFile, String> externalFilesWithMappedURLs = bulkRemoteUrlMappings
                (externalFiles, "get_beer");

        for (var entry : externalFilesWithMappedURLs.entrySet()) {
            BaseFile baseFile = entry.getKey();
            String mappedURL = entry.getValue();

            String externalId = baseFile.getFileId();
            beerImageRepository.findByImageUrl(mappedURL).ifPresent(beerImage -> {
                if (beerImage.getRemoteId() == null) {
                    beerImage.setRemoteId(externalId);
                    beerImageRepository.save(beerImage);
                }
            });
        }
    }

    private void updateStoreImageWithRemoteIDs() {
        List<BaseFile> externalFiles = imageKitRepository.findAllIn("/store");

        for (var file : externalFiles) {
            String externalId = file.getFileId();
            storeImageRepository.findByImageUrl(file.getUrl()).ifPresent(storeImage -> {
                if (storeImage.getRemoteId() == null) {
                    storeImage.setRemoteId(externalId);
                    storeImageRepository.save(storeImage);
                }
            });
        }
    }

    private <T extends ImageModel> void updateUrlWithUpdatedAt(CrudRepository<T, Long> imageRepository) {
        for (var image : imageRepository.findAll()) {
            long updatedAt = getUpdatedAt(image.getRemoteId());
            String newURL = image.getImageUrl() + "?updatedAt=" + updatedAt;
            image.setImageUrl(newURL);
            imageRepository.save(image);
        }
    }

    private Map<BaseFile, String> bulkRemoteUrlMappings(List<BaseFile> files, String transformationName) {
        List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", transformationName)));
        return files.stream().collect(Collectors.toMap(key -> key, value -> {
            Map<String, Object> options = new HashMap<>();
            options.put("path", value.getFilePath());
            options.put("transformation", transformation);
            return ImageKit.getInstance().getUrl(options);
        }));
    }
}
