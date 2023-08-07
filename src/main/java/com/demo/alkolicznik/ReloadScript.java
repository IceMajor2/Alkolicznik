package com.demo.alkolicznik;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.ImageRepository;
import io.imagekit.sdk.models.BaseFile;
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
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

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

	private static boolean turnOn = false;

	private ImageKitRepository imageKitRepository;

	private ImageRepository imageRepository;

	private String imageKitPath;

	public ReloadScript(ImageKitRepository imageKitRepository, ImageRepository imageRepository, String imageKitPath) {
		this.imageKitRepository = imageKitRepository;
		this.imageRepository = imageRepository;
		this.imageKitPath = imageKitPath;
	}

	@Override
	public void run(String... args) throws Exception {
		if (turnOn) {
			LOGGER.info("Reloading ImageKit directory");
			bulkDeleteRemoteImages();
			bulkSendImagesToRemote();
			LOGGER.info("Successfully reloaded ImageKit directory");
			updateDatabaseTableWithRemoteIds();
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

	private void bulkDeleteRemoteImages() {
		LOGGER.info("Deleting ImageKit images");
		imageKitRepository.deleteAllIn("/beer");
		LOGGER.info("Success!");
	}

	@SneakyThrows
	private void bulkSendImagesToRemote() {
		LOGGER.info("Sending all files in '/images' directory into remote's '/beer'");
		File[] imageDirectory = new File(new ClassPathResource("/images/beer").getURI().getRawPath()).listFiles();
		for (File image : imageDirectory) {
			imageKitRepository.save(image.getAbsolutePath(), "/beer", image.getName());
		}
		LOGGER.info("Success!");
	}

	private void updateDatabaseTableWithRemoteIds() {
		LOGGER.info("Fetching beer images' external ids");

		List<BaseFile> externalFiles = imageKitRepository.findAllIn("/beer");
		Map<BaseFile, String> externalFilesWithMappedURLs = imageKitRepository
				.bulkRemoteUrlMappings(externalFiles);

		for (var entry : externalFilesWithMappedURLs.entrySet()) {
			BaseFile keyBaseFile = entry.getKey();
			String mappedURL = entry.getValue();

			String externalId = keyBaseFile.getFileId();
			imageRepository.findByImageUrl(mappedURL).ifPresent(beerImage -> {
				if (beerImage.getRemoteId() == null) {
					beerImage.setRemoteId(externalId);
					imageRepository.save(beerImage);
				}
			});
		}
		LOGGER.info("Remote IDs were successfully saved in the database");
	}
}
