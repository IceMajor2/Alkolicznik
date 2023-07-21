package com.demo.alkolicznik;

import com.demo.alkolicznik.api.services.ImageService;
import com.demo.alkolicznik.models.ImageModel;
import io.imagekit.sdk.models.BaseFile;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Map;

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
public class ReloadScript implements CommandLineRunner {

    public static void main(String[] args) {
        turnOn = true;
        SpringApplication.run(AlkolicznikApplication.class, args).close();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadScript.class);
    private ImageService imageService;
    private static boolean turnOn = false;

    public ReloadScript(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void run(String... args) throws Exception {
        if(turnOn) {
            LOGGER.info("Reloading ImageKit directory");
            bulkDeleteRemoteImages();
            bulkSendImagesToRemote();
            LOGGER.info("Successfully reloaded ImageKit directory");
            updateDatabaseTableWithRemoteIds();
        }
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Qualifier("dataSource") final DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/delete.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("/schema.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("/data.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

    private void bulkDeleteRemoteImages() {
        LOGGER.info("Deleting ImageKit images");
        imageService.deleteAllExternal("/beer");
        LOGGER.info("Success!");
    }

    @SneakyThrows
    private void bulkSendImagesToRemote() {
        LOGGER.info("Sending all files in '/images' directory into remote's '/beer'");
        File[] imageDirectory = new File(new ClassPathResource("/images").getURI().getRawPath()).listFiles();
        for (File image : imageDirectory) {
            imageService.upload(image.getAbsolutePath(), image.getName());
        }
        LOGGER.info("Success!");
    }

    private void updateDatabaseTableWithRemoteIds() {
        LOGGER.info("Fetching beer images' external ids");

        List<BaseFile> externalFiles = imageService.getExternalFiles("/beer");
        Map<BaseFile, String> externalFilesWithMappedURLs = imageService.mapExternalFilesURL(externalFiles);

        for (var entry : externalFilesWithMappedURLs.entrySet()) {
            BaseFile keyBaseFile = entry.getKey();
            String mappedURL = entry.getValue();

            String externalId = keyBaseFile.getFileId();
            ImageModel modelToUpdate = imageService.findByUrl(mappedURL);

            if (modelToUpdate.getExternalId() == null) {
                imageService.updateExternalId(modelToUpdate, externalId);
            }
        }
        LOGGER.info("Remote IDs were successfully saved in the database");
    }
}
