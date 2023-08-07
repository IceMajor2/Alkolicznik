package com.demo.alkolicznik.api.services;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageProportionsInvalidException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.vaadin.flow.component.html.Image;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

	private ImageKitRepository imageKitRepository;

	private BeerRepository beerRepository;

	private BeerImageRepository beerImageRepository;

	private String imageKitPath;

	public ImageService(ImageKitRepository imageKitRepository, BeerRepository beerRepository, BeerImageRepository beerImageRepository, String imageKitPath) {
		this.imageKitRepository = imageKitRepository;
		this.beerRepository = beerRepository;
		this.beerImageRepository = beerImageRepository;
		this.imageKitPath = imageKitPath;
	}

	@SneakyThrows
	public void add(Beer beer, String imagePath) {
		// instantiate BufferedImage and check its proportions
		File file = new File(imagePath);
		if (!file.exists()) {
			throw new FileNotFoundException(imagePath);
		}
		if (!proportionsOk(ImageIO.read(file))) {
			throw new ImageProportionsInvalidException();
		}
		BeerImage beerImage = (BeerImage) imageKitRepository.save(imagePath, "/beer",
				this.createImageFilename
						(beer, this.extractFileExtensionFromPath(imagePath)));
		beer.setImage(beerImage);
		beerImage.setBeer(beer);
		beerImageRepository.save(beerImage);
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
		beerImageRepository.save(beerImage);
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
		imageKitRepository.delete(beerImage);
		beer.setImage(null);
		beerImageRepository.deleteById(beerImage.getId());
		return new ImageDeleteDTO(beer);
	}

	public void deleteAllRemoteIn(String path) {
		imageKitRepository.deleteAllIn(path);
	}

	public ImageDeleteDTO delete(Long beerId) {
		return this.delete(beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId)));
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
}
