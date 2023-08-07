package com.demo.alkolicznik.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.alkolicznik.utils.TestUtils;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultList;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static com.demo.alkolicznik.utils.TestUtils.getRawPathToClassPathResource;

@Configuration
@Profile("image")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ImageKitConfig {

	private TestUtils testUtils;

	private static String imageKitPath;

	private ImageKit imageKit;

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageKitConfig.class);

	private static final List<String> expectedBeerImages = List.of
			("tyskie-gronie-0.65.png", "zubr-0.5.png",
					"komes-porter-malinowy-0.33.jpg", "miloslaw-biale-0.5.jpg");

	@Autowired
	public ImageKitConfig(TestUtils testUtils) {
		this.testUtils = testUtils;
	}

	@Autowired
	public void setImageKitPath(String imageKitPath) {
		ImageKitConfig.imageKitPath = imageKitPath;
	}

	@PostConstruct
	public void init() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
		LOGGER.info("Reloading ImageKit's directory");
		setImageKit();
		deletePostedByTestImages();
		sendInitialImages();
	}

	private void deletePostedByTestImages() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IllegalAccessException, InstantiationException {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(imageKitPath + "/beer");
		ResultList resultList = this.imageKit.getFileList(getFileListRequest);

		for (BaseFile baseFile : resultList.getResults()) {
			if (expectedBeerImages.contains(baseFile.getName())) {
				LOGGER.info("'%s' was found (ID: %s). Not to be DELETED"
						.formatted(baseFile.getName(), baseFile.getFileId()));
				continue;
			}
			LOGGER.info("Deleting '%s'... (ID: %s)"
					.formatted(baseFile.getName(), baseFile.getFileId()));
			this.imageKit.deleteFile(baseFile.getFileId());
		}
	}

	private void sendInitialImages() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException, IllegalAccessException, InstantiationException {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(imageKitPath + "/beer");

		Map<String, String> baseFiles = this.imageKit.getFileList(getFileListRequest)
				.getResults()
				.stream()
				.collect(Collectors.toMap(BaseFile::getName, BaseFile::getFileId));
		File[] testDir =
				new File(getRawPathToClassPathResource("/data_img/init_data")).listFiles();

		for (File image : testDir) {
			if (baseFiles.containsKey(image.getName())) {
				LOGGER.info("'%s' was found (ID: %s). Not to be SENT"
						.formatted(image.getName(), baseFiles.get(image.getName())));
				continue;
			}
			byte[] bytes = Files.readAllBytes(image.toPath());
			FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, image.getName());
			fileCreateRequest.setUseUniqueFileName(false);
			fileCreateRequest.setFolder(imageKitPath + "/beer");
			Result result = this.imageKit.upload(fileCreateRequest);
			LOGGER.info("'%s' was successfully sent. (ID: %s)"
					.formatted(result.getName(), result.getFileId()));
		}
	}

	private void setImageKit() {
		this.imageKit = ImageKit.getInstance();
		String endpoint = "https://ik.imagekit.io/icemajor";
		String publicKey = "public_YpQHYFb3+OX4R5aHScftYE0H0N8=";
		try {
			this.imageKit.setConfig(new io.imagekit.sdk.config.Configuration(publicKey,
					Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
					endpoint));
		}
		catch (IOException e) {
			throw new RuntimeException("Could not read secured file");
		}
	}

	public static String extractFilenameFromUrl(String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}

	public static String getRemoteId(String filename) {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(imageKitPath + "/beer");
		ResultList resultList = null;
		try {
			resultList = ImageKit.getInstance().getFileList(getFileListRequest);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (BaseFile baseFile : resultList.getResults()) {
			if (baseFile.getName().equals(filename)) {
				return baseFile.getFileId();
			}
		}
		return null;
	}
}
