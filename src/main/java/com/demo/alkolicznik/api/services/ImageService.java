package com.demo.alkolicznik.api.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.ImageProportionsInvalidException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.BeerImageRepository;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreImageRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageService {

	private ImageKitRepository imageKitRepository;

	private BeerImageRepository beerImageRepository;

	private StoreImageRepository storeImageRepository;

	private BeerRepository beerRepository;

	private StoreRepository storeRepository;

	public ImageModelResponseDTO getBeerImage(Long beerId) {
		Beer beer = beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId));
		BeerImage image = beer.getImage()
				.orElseThrow(() -> new ImageNotFoundException(BeerImage.class));
		return new ImageModelResponseDTO(image);
	}

	public ImageModelResponseDTO getStoreImage(Long storeId) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new StoreNotFoundException(storeId));
		StoreImage image = store.getImage()
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		return new ImageModelResponseDTO(image);
	}

	public ImageModelResponseDTO getStoreImage(String storeName) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		StoreImage image = storeImageRepository.findByStoreName(storeName)
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		return new ImageModelResponseDTO(image);
	}

	Optional<StoreImage> findStoreImage(String storeName) {
		return storeImageRepository.findByStoreName(storeName);
	}

	@SneakyThrows
	public void addBeerImage(Beer beer, String imagePath) {
		// instantiate BufferedImage and check its proportions
		File file = new File(imagePath);
		if (!file.exists()) {
			throw new FileNotFoundException(imagePath);
		}
		if (!proportionsOk(ImageIO.read(file))) {
			throw new ImageProportionsInvalidException();
		}
		BeerImage beerImage = (BeerImage) imageKitRepository.save(
				imagePath, "/beer", this.createImageFilename(beer,
						this.extractFileExtensionFromPath(imagePath)), BeerImage.class);
		beer.setImage(beerImage);
		beerImage.setBeer(beer);
		beerImageRepository.save(beerImage);
	}

	public void deleteStoreImage(StoreImage storeImage) {
		imageKitRepository.delete(storeImage);
		storeImageRepository.delete(storeImage);
	}

	private StoreImage addStoreImage(String storeName, String imagePath) {
		File file = new File(imagePath);
		if (!file.exists()) throw new FileNotFoundException(imagePath);

		StoreImage storeImage = (StoreImage) imageKitRepository.save(imagePath, "/store",
				createImageFilename(storeName, extractFileExtensionFromPath(imagePath)), StoreImage.class);
		storeImage.setStoreName(storeName);
		return storeImageRepository.save(storeImage);
	}

	public ImageModelResponseDTO addStoreImage(Store store, String imagePath) {
		StoreImage added = addStoreImage(store.getName(), imagePath);
		store.setImage(added);
		return new ImageModelResponseDTO(added);
	}

	public ImageModelResponseDTO addStoreImage(String storeName, ImageRequestDTO request) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		return new ImageModelResponseDTO(addStoreImage(storeName, request.getImagePath()));
	}

	/**
	 * Deletes beer's image from database.
	 *
	 * @throws ImageNotFoundException when passed beer does not have an image assigned
	 */
	@SneakyThrows
	public ImageDeleteDTO delete(Beer beer) {
		BeerImage beerImage = beer.getImage().orElseThrow(() ->
				new ImageNotFoundException(BeerImage.class));
		imageKitRepository.delete(beerImage);
		beer.setImage(null);
		beerImageRepository.deleteById(beerImage.getId());
		return new ImageDeleteDTO(beer);
	}

	public ImageDeleteDTO delete(Long beerId) {
		return this.delete(beerRepository.findById(beerId)
				.orElseThrow(() -> new BeerNotFoundException(beerId)));
	}

	public <T extends ImageModel> ImageModel save(T image, Class<T> imgClass) {
		if (imgClass.equals(BeerImage.class))
			return beerImageRepository.save((BeerImage) image);
		if (imgClass.equals(StoreImage.class))
			return storeImageRepository.save((StoreImage) image);
		return null;
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

	private String createImageFilename(String storeName, String extension) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder
				.append(storeName.toLowerCase().replace(' ', '-'))
				.append('.')
				.append(extension);
		return stringBuilder.toString();
	}

	public String extractFileExtensionFromPath(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}
}
