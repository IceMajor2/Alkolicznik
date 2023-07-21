package com.demo.alkolicznik;

import com.demo.alkolicznik.api.services.ImageService;
import com.demo.alkolicznik.models.ImageModel;
import io.imagekit.sdk.models.BaseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AppStartupCommandLineRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AppStartupCommandLineRunner.class);
    private boolean turnOn = true;
    private ImageService imageService;

    public AppStartupCommandLineRunner(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void run(String... args) throws Exception {
        if(turnOn) {
            LOG.info("Fetching beer images' external ids");

            List<BaseFile> externalFiles = imageService.getExternalFiles("/beer");
            Map<BaseFile, String> externalFilesWithMappedURLs = imageService.mapExternalFilesURL(externalFiles);

            for(var entry : externalFilesWithMappedURLs.entrySet()) {
                BaseFile keyBaseFile = entry.getKey();
                String mappedURL = entry.getValue();

                String externalId = keyBaseFile.getFileId();
                ImageModel modelToUpdate = imageService.findByUrl(mappedURL);

                if(modelToUpdate.getExternalId() == null) {
                    imageService.updateExternalId(modelToUpdate, externalId);
                }
            }
            LOG.info("External ids were successfully saved in the database");
        }
    }
}
