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
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

@Component
public class ImageKitRepository {

	private ImageKit imageKit;

	public ImageKitRepository() {
		this.imageKit = ImageKit.getInstance();
		setConfig();
	}

	public ImageModel save(String srcPath, String remotePath, String filename) {
		return save(srcPath, remotePath, filename, true);
	}

	/**
	 * This procedure reads the file from a {@code path}, converts it into
	 * an ImageKit-library-uploadable and sends it to an external image hosting.
	 */
	@SneakyThrows
	public ImageModel save(String srcPath, String remotePath, String filename, boolean urlTransform) {
		Result result = upload(srcPath, remotePath, filename);
		if (urlTransform) {
			// get link with named transformation 'get_beer'
			List<Map<String, String>> transformation = new ArrayList<>
					(List.of(Map.of("named", "get_beer")));
			Map<String, Object> options = new HashMap<>();
			options.put("path", result.getFilePath());
			options.put("transformation", transformation);
			return new BeerImage(this.imageKit.getUrl(options), result.getFileId());
		}
		return new BeerImage(this.imageKit.getUrl(null), result.getFileId());
	}

	@SneakyThrows
	private Result upload(String srcPath, String remotePath, String filename) {
		// send to server
		byte[] bytes = Files.readAllBytes(Paths.get(srcPath));
		FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, filename);
		// prevent adding a random string to the end of the filename
		fileCreateRequest.setUseUniqueFileName(false);
		// set folder into which image will be uploaded
		fileCreateRequest.setFolder(remotePath);
		return this.imageKit.upload(fileCreateRequest);
	}

	@SneakyThrows
	private String mapUrl(Map<String, Object> options) {
		return imageKit.getUrl(options);
	}

	@SneakyThrows
	public List<BaseFile> findAllIn(String path) {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(path);
		return imageKit.getFileList(getFileListRequest).getResults();
	}

	@SneakyThrows
	public void delete(ImageModel image) {
		imageKit.deleteFile(image.getRemoteId());
	}

	@SneakyThrows
	public void deleteAllIn(String path) {
		var deleteIds = findAllIn(path).stream()
				.map(BaseFile::getFileId)
				.toList();
		if (deleteIds.isEmpty()) {
			return;
		}
		imageKit.bulkDeleteFiles(deleteIds);
	}

	public Map<BaseFile, String> bulkRemoteUrlMappings(List<BaseFile> files) {
		List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", "get_beer")));
		return files.stream().collect(Collectors.toMap(key -> key, value -> {
			Map<String, Object> options = new HashMap<>();
			options.put("path", value.getFilePath());
			options.put("transformation", transformation);
			return imageKit.getUrl(options);
		}));
	}

	private void setConfig() {
		String endpoint = "https://ik.imagekit.io/icemajor";
		String publicKey = "public_YpQHYFb3+OX4R5aHScftYE0H0N8=";
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
