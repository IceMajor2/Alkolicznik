package com.demo.alkolicznik.api.services;

import java.util.List;
import java.util.Optional;

import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.PropertiesMissingException;
import com.demo.alkolicznik.exceptions.classes.store.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.ModelDtoConverter;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import static com.demo.alkolicznik.utils.ModelDtoConverter.storeListToDtoList;

@Service
@AllArgsConstructor
public class StoreService {

	private StoreRepository storeRepository;

	private ImageService imageService;

	public List<StoreResponseDTO> getStores(String city) {
		if (!storeRepository.existsByCity(city)) {
			throw new NoSuchCityException(city);
		}
		return storeListToDtoList(storeRepository.findAllByCityOrderByIdAsc(city));
	}

	public List<StoreResponseDTO> getStores() {
		return storeListToDtoList(storeRepository.findAllByOrderByIdAsc());
	}

	public StoreResponseDTO get(Long storeId) {
		return new StoreResponseDTO(storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId))
		);
	}

	public StoreResponseDTO add(StoreRequestDTO requestDTO) {
		Optional<StoreImage> optImage = imageService.findStoreImage(requestDTO.getName());
		Store store = ModelDtoConverter.convertToModelWithImage(requestDTO, optImage);
		if (storeRepository.exists(store)) throw new StoreAlreadyExistsException();

		String imagePath = requestDTO.getImagePath();
		if (imagePath != null) {
			if (optImage.isPresent()) {
				imageService.deleteStoreImage(optImage.get());
			}
			imageService.addStoreImage(store, imagePath);
		}
		return new StoreResponseDTO(storeRepository.save(store));
	}

	public StoreResponseDTO replace(Long storeId, StoreRequestDTO requestDTO) {
		Store toOverwrite = checkForPutConditions(storeId, requestDTO);
		Store overwritten = createOverwrittenModel(requestDTO, toOverwrite);

		if (storeRepository.exists(overwritten)) {
			throw new StoreAlreadyExistsException();
		}
		// for each PUT request all the previous
		// beer prices in this store MUST be deleted
		overwritten.deleteAllPrices();
		if(!overwritten.getName().equals(toOverwrite.getName())
				&& storeRepository.countByName(toOverwrite.getName()) == 1) {
			toOverwrite.getImage()
					.ifPresent(storeImage -> imageService.deleteStoreImage(storeImage));
		}
		return new StoreResponseDTO(storeRepository.save(overwritten));
	}
	
	private Store createOverwrittenModel(StoreRequestDTO requestDTO, Store toOverwrite) {
		Store overwritten = ModelDtoConverter.convertToModelNoImage(requestDTO);
		overwritten.setId(toOverwrite.getId());
		overwritten.setPrices(toOverwrite.getPrices());
		var newImage = imageService.findStoreImage(requestDTO.getName());
		newImage.ifPresent(img -> overwritten.setImage(img));
		return overwritten;
	}

	public StoreResponseDTO update(Long storeId, StoreUpdateDTO updateDTO) {
		Store store = checkForPatchConditions(storeId, updateDTO);
		Store updated = updateFieldsOnPatch(store, updateDTO);
		if (storeRepository.exists(updated)) {
			throw new StoreAlreadyExistsException();
		}
		updated.deleteAllPrices();
		return new StoreResponseDTO(storeRepository.save(updated));
	}

	public StoreDeleteDTO delete(Long storeId) {
		Store toDelete = storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId));
		toDelete.deleteAllPrices();
		storeRepository.delete(toDelete);
		return new StoreDeleteDTO(toDelete);
	}

	public StoreDeleteDTO delete(StoreRequestDTO store) {
		Store toDelete = storeRepository.findByStoreRequest(store)
				.orElseThrow(() -> new StoreNotFoundException(store.getName(), store.getCity(), store.getStreet()));
		return this.delete(toDelete.getId());
	}

	private Store checkForPatchConditions(Long storeId, StoreUpdateDTO updateDTO) {
		if (updateDTO.propertiesMissing()) {
			throw new PropertiesMissingException();
		}
		return checkForUpdateConditions(storeId, updateDTO);
	}

	private Store checkForPutConditions(Long storeId, StoreRequestDTO requestDTO) {
		return checkForUpdateConditions(storeId, StoreUpdateDTO.convertFromRequest(requestDTO));
	}

	private Store checkForUpdateConditions(Long storeId, StoreUpdateDTO updateDTO) {
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new StoreNotFoundException(storeId)
		);
		if (!updateDTO.anythingToUpdate(store)) {
			throw new ObjectsAreEqualException();
		}
		return store;
	}

	private Store updateFieldsOnPatch(Store toUpdate, StoreUpdateDTO updateDTO) {
		String updatedName = updateDTO.getName();
		String updatedCity = updateDTO.getCity();
		String updatedStreet = updateDTO.getStreet();
		if (updatedName != null) {
			toUpdate.setName(updatedName);
		}
		if (updatedCity != null) {
			toUpdate.setCity(updatedCity);
		}
		if (updatedStreet != null) {
			toUpdate.setStreet(updatedStreet);
		}
		return toUpdate;
	}
}
