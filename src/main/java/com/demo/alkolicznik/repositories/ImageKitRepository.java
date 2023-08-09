package com.demo.alkolicznik.repositories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.NotFoundException;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.DeleteFolderRequest;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

@Component
public class ImageKitRepository {

	private ImageKit imageKit;

	private String imageKitPath;

	public ImageKitRepository(String imageKitPath) {
		this.imageKit = ImageKit.getInstance();
		this.imageKitPath = imageKitPath;
		setConfig();
	}

	/**
	 * This procedure reads the file from a {@code path}, converts it into
	 * an ImageKit-library-uploadable and sends it to an external image hosting.
	 */
	@SneakyThrows
	public ImageModel save(String srcPath, String remotePath, String filename, Class<? extends ImageModel> imgClass) {
		Result result = upload(srcPath, remotePath, filename);
		// get link with named transformation
		var options = getOptions(result, imgClass);
		if (imgClass.equals(StoreImage.class))
			return new StoreImage(mapUrl(options), result.getFileId());
		if (imgClass.equals(BeerImage.class))
			return new BeerImage(mapUrl(options), result.getFileId());
		return null;
	}

	private static Map<String, Object> getOptions(Result result, Class<? extends ImageModel> imgClass) {
		List<Map<String, String>> transformation = new ArrayList<>
				(List.of(Map.of(
						"named", imgClass.equals(StoreImage.class)
								? "get_store" : imgClass.equals(BeerImage.class)
								? "get_beer" : null
				)));
		Map<String, Object> options = new HashMap<>();
		options.put("path", result.getFilePath());
		options.put("transformation", transformation);
		return options;
	}

	@SneakyThrows
	private Result upload(String srcPath, String remotePath, String filename) {
		// send to server
		byte[] bytes = Files.readAllBytes(Paths.get(srcPath));
		FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, filename);
		// prevent adding a random string to the end of the filename
		fileCreateRequest.setUseUniqueFileName(false);
		// set folder into which image will be uploaded
		fileCreateRequest.setFolder(imageKitPath + remotePath);
		return this.imageKit.upload(fileCreateRequest);
	}

	@SneakyThrows
	private String mapUrl(Map<String, Object> options) {
		return imageKit.getUrl(options);
	}

	@SneakyThrows
	public List<BaseFile> findAllIn(String path) {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(imageKitPath + path);
		return imageKit.getFileList(getFileListRequest).getResults();
	}

	@SneakyThrows
	public void delete(ImageModel image) {
		imageKit.deleteFile(image.getRemoteId());
	}

	@SneakyThrows
	public void deleteAllIn(String path) {
		var deleteIds = findAllIn(imageKitPath + path).stream()
				.map(BaseFile::getFileId)
				.toList();
		if (deleteIds.isEmpty()) {
			return;
		}
		imageKit.bulkDeleteFiles(deleteIds);
	}

	@SneakyThrows
	public void deleteFolder(String path) throws NotFoundException {
		DeleteFolderRequest deleteFolderRequest = new DeleteFolderRequest();
		deleteFolderRequest.setFolderPath(imageKitPath + path);
		imageKit.deleteFolder(deleteFolderRequest);
	}

	public Map<BaseFile, String> bulkRemoteUrlMappings(List<BaseFile> files, String transformationName) {
		List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", transformationName)));
		return files.stream().collect(Collectors.toMap(key -> key, value -> {
			Map<String, Object> options = new HashMap<>();
			options.put("path", value.getFilePath());
			options.put("transformation", transformation);
			return imageKit.getUrl(options);
		}));
	}

	private void setConfig() {
		String endpoint = "https://ik.imagekit.io/alkolicznik";
		String publicKey = "public_9bnA9mQhgiGpder50E8rqIB98uM=";
		try {
			imageKit.setConfig(new Configuration(publicKey,
					Files.readAllLines(Paths.get("secure" + File.separator + "api_key.txt")).get(0),
					endpoint));
		}
		catch (IOException e) {
			throw new RuntimeException("Could not read secured file");
		}
	}
}
