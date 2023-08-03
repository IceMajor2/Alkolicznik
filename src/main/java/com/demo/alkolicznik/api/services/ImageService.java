package com.demo.alkolicznik.api.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageProportionsInvalidException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageRepository;
import com.vaadin.flow.component.html.Image;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

	private BeerRepository beerRepository;

	private ImageRepository imageRepository;

	private String imageKitPath;

	private ImageKit imageKit;

	public ImageService(ImageRepository imageRepository, BeerRepository beerRepository, String imageKitPath) {
		this.imageRepository = imageRepository;
		this.beerRepository = beerRepository;
		this.imageKitPath = imageKitPath;

		this.imageKit = ImageKit.getInstance();
		setConfig();
	}

	/**
	 * This procedure reads the file from a {@code path}, converts it into
	 * an ImageKit-library-uploadable and sends it to an external image hosting.
	 */
	@SneakyThrows
	public BeerImage upload(String path, String filename) {
		// instantiate BufferedImage and check its proportions
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException(path);
		}
		if (!proportionsOk(ImageIO.read(file))) {
			throw new ImageProportionsInvalidException();
		}
		// send to server
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, filename);
		// prevent adding a random string to the end of the filename
		fileCreateRequest.setUseUniqueFileName(false);
		// set folder into which image will be uploaded
		fileCreateRequest.setFolder(imageKitPath);

		Result result = this.imageKit.upload(fileCreateRequest);

		// get link with transformation 'get_beer'
		List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", "get_beer")));
		Map<String, Object> options = new HashMap<>();
		options.put("path", result.getFilePath());
		options.put("transformation", transformation);

		// should be returned as ResponseDTO
		return new BeerImage(imageKit.getUrl(options), result.getFileId());
	}

	public ImageModelResponseDTO getBeerImage(Long beerId) {
		Beer beer = beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId));
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException());
		return new ImageModelResponseDTO(image);
	}

	/**
	 * Call to this method will return Vaadin's {@code Image} component.
	 * Its fetching type is lazy. If it's the 1st component request for
	 * this image, then it'll also be created.
	 * Used for frontend display.
	 */
	public Image getVaadinBeerImage(Long beerId) {
		Beer beer = beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId));
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException());

		// Lazy fetching. Create component (and save), if not done previously.
		if (image.getImageComponent() == null) {
			Image component = createJavaImage(image);
			saveJavaBeerImage(beer, component);
		}
		return beer.getImage().get().getImageComponent();
	}

	/**
	 * Creates Vaadin component.
	 */
	private Image createJavaImage(BeerImage image) {
		return new Image(image.getImageUrl(), "No image");
	}

	/**
	 * Updates the database with actual image as byte array.
	 *
	 * @param beer  to get image model from
	 * @param image to update the image model with
	 */
	private void saveJavaBeerImage(Beer beer, Image image) {
		BeerImage beerImage = beer.getImage().get();
		beerImage.setImageComponent(image);
		imageRepository.save(beerImage);
	}

	/**
	 * Deletes beer's image from database.
	 *
	 * @throws ImageNotFoundException when passed beer does not have an image assigned
	 */
	@SneakyThrows
	public ImageDeleteDTO delete(Beer beer) {
		BeerImage beerImage = beer.getImage().orElseThrow(() ->
				new ImageNotFoundException());
		imageKit.deleteFile(beerImage.getRemoteId());
		beer.setImage(null);
		imageRepository.deleteById(beerImage.getId());
		return new ImageDeleteDTO(beer);
	}

	public ImageDeleteDTO delete(Long beerId) {
		return this.delete(beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId)));
	}

	@SneakyThrows
	public void deleteAllExternal(String relativePath) {
		var deleteIds = getExternalFiles(relativePath).stream().map(BaseFile::getFileId).toList();
		if (deleteIds.isEmpty()) {
			return;
		}
		imageKit.bulkDeleteFiles(deleteIds);
	}

	public BeerImage findByUrl(String url) {
		return imageRepository.findByImageUrl(url).orElseThrow(() ->
				new ImageNotFoundException());
	}

	public ImageModelResponseDTO updateExternalId(BeerImage toUpdate, String externalId) {
		toUpdate.setRemoteId(externalId);
		return new ImageModelResponseDTO(imageRepository.save(toUpdate));
	}

	private boolean proportionsOk(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		double widthScaled = (double) width / 3;
		double heightScaled = (double) height / 3;

		double heightExact = (widthScaled * 7) / 3;
		double heightVicinity = heightExact * 0.2;

		double lowerBound = heightVicinity;
		double upperBound = heightVicinity * 3;

		if (heightScaled >= heightExact - lowerBound && heightScaled <= heightExact + upperBound) {
			return true;
		}
		return false;
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

	/**
	 * Get all files in ImageKit's directory.
	 */
	@SneakyThrows
	public List<BaseFile> getExternalFiles(String relativePath) {
		GetFileListRequest getFileListRequest = new GetFileListRequest();
		getFileListRequest.setPath(relativePath);
		return imageKit.getFileList(getFileListRequest).getResults();
	}

	/**
	 * Returns a map with an external file as a key and mapped url as value.
	 */
	public Map<BaseFile, String> mapExternalFilesURL(List<BaseFile> files) {
		List<Map<String, String>> transformation = new ArrayList<>(List.of(Map.of("named", "get_beer")));
		return files.stream().collect(Collectors.toMap(key -> key, value -> {
			Map<String, Object> options = new HashMap<>();
			options.put("path", value.getFilePath());
			options.put("transformation", transformation);
			return imageKit.getUrl(options);
		}));
	}

	public String createImageFilename(Beer beer, String extension) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder
				.append(beer.getFullName().toLowerCase().replace(' ', '-'))
				.append('-')
				.append(beer.getVolume())
				.append('.')
				.append(extension);
		return stringBuilder.toString();
	}

	public String extractFileExtensionFromPath(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	public void add(Beer beer, String imagePath) {
		BeerImage beerImage = this.upload(
				imagePath,
				this.createImageFilename(
						beer, this.extractFileExtensionFromPath(imagePath)
				)
		);
		beer.setImage(beerImage);
		beerImage.setBeer(beer);
		imageRepository.save(beerImage);
	}
}
