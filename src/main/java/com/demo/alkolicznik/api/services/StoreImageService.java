package com.demo.alkolicznik.api.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.exceptions.classes.FileIsNotImageException;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.image.ImageAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.image.ImageNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.ImageKitRepository;
import com.demo.alkolicznik.repositories.StoreImageRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import static com.demo.alkolicznik.utils.ModelDtoConverter.storeImageListToDtoList;
import static com.demo.alkolicznik.utils.Utils.getExtensionFromPath;
import static com.demo.alkolicznik.utils.Utils.getImage;

@Service
@AllArgsConstructor
public class StoreImageService {

	private StoreRepository storeRepository;

	private StoreImageRepository storeImageRepository;

	private ImageKitRepository imageKitRepository;

	public ImageModelResponseDTO get(Long storeId) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new StoreNotFoundException(storeId));
		StoreImage image = store.getImage()
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		return new ImageModelResponseDTO(image);
	}

	public ImageModelResponseDTO get(String storeName) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		StoreImage image = storeImageRepository.findByStoreName(storeName)
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		return new ImageModelResponseDTO(image);
	}

	public List<ImageModelResponseDTO> getAll() {
		return storeImageListToDtoList(storeImageRepository.findAll());
	}

	public ImageModelResponseDTO add(Store store, String imagePath) {
		StoreImage added = this.add(store.getName(), imagePath);
		store.setImage(added);
		return new ImageModelResponseDTO(added);
	}

	public ImageModelResponseDTO add(String storeName, ImageRequestDTO request) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		return new ImageModelResponseDTO(
				this.add(storeName, request.getImagePath()));
	}

	private StoreImage add(String storeName, String imagePath) {
		File file = new File(imagePath);
		fileCheck(file);
		// TODO: adjust image's size
		if (storeImageRepository.existsByStoreName(storeName))
			throw new ImageAlreadyExistsException();
		StoreImage storeImage = (StoreImage) imageKitRepository.save(imagePath, "/store",
				createFilename(storeName, getExtensionFromPath(imagePath)), StoreImage.class);
		storeImage.setStoreName(storeName);
		return storeImageRepository.save(storeImage);
	}

	public ImageModelResponseDTO update(String storeName, ImageRequestDTO request) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		StoreImage image = storeImageRepository.findByStoreName(storeName)
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		this.delete(image);
		return this.add(storeName, request);
	}

	public ImageDeleteDTO delete(String storeName) {
		if (!storeRepository.existsByName(storeName))
			throw new StoreNotFoundException(storeName);
		StoreImage image = storeImageRepository.findByStoreName(storeName)
				.orElseThrow(() -> new ImageNotFoundException(StoreImage.class));
		return this.delete(image);
	}

	public ImageDeleteDTO delete(StoreImage image) {
		imageKitRepository.delete(image);
		storeImageRepository.delete(image);
		return new ImageDeleteDTO(image);
	}

	private void fileCheck(File file) {
		if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
		BufferedImage image = getImage(file);
		if(image == null) throw new FileIsNotImageException();
	}

	private String createFilename(String storeName, String extension) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder
				.append(storeName.toLowerCase().replace(' ', '-'))
				.append('.')
				.append(extension);
		return stringBuilder.toString();
	}

	public Optional<StoreImage> findByStoreName(String name) {
		return storeImageRepository.findByStoreName(name);
	}
}
