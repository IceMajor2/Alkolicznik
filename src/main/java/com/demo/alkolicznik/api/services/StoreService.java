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

	private StoreImageService imageService;

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
		Optional<StoreImage> optImage = imageService.findByStoreName(requestDTO.getName());
		Store store = ModelDtoConverter.convertToModelWithImage(requestDTO, optImage);
		if (storeRepository.exists(store)) throw new StoreAlreadyExistsException();
		return new StoreResponseDTO(storeRepository.save(store));
	}

	public StoreResponseDTO replace(Long storeId, StoreRequestDTO requestDTO) {
		Store toOverwrite = checkForPutConditions(storeId, requestDTO);
		Store overwritten = createOverwrittenModel(requestDTO, toOverwrite);

		if (storeRepository.exists(overwritten)) {
			throw new StoreAlreadyExistsException();
		}
		// for each PUT request all the previous
		// beer prices MUST be deleted
		overwritten.deleteAllPrices();
		// if the name of the store was unique and is being
		// changed right now, then delete previous image
		if (!overwritten.getName().equals(toOverwrite.getName())
				&& storeRepository.countByName(toOverwrite.getName()) == 1) {
			toOverwrite.getImage()
					.ifPresent(storeImage -> imageService.delete(storeImage.getStoreName()));
			toOverwrite.setImage(null);
		}
		return new StoreResponseDTO(storeRepository.save(overwritten));
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
		if (storeRepository.countByName(toDelete.getName()) == 1) {
			toDelete.getImage().ifPresent(
					storeImage -> imageService.delete(storeImage.getStoreName()));
//			toDelete.setImage(null);
		}
		storeRepository.delete(toDelete);
		return new StoreDeleteDTO(toDelete);
	}

	public StoreDeleteDTO delete(StoreRequestDTO store) {
		Store toDelete = storeRepository.findByStoreRequest(store)
				.orElseThrow(() -> new StoreNotFoundException
						(store.getName(), store.getCity(), store.getStreet()));
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

	private Store createOverwrittenModel(StoreRequestDTO requestDTO, Store toOverwrite) {
		Optional<StoreImage> image = imageService.findByStoreName(requestDTO.getName());
		Store overwritten = ModelDtoConverter.convertToModelWithImage(requestDTO, image);
		overwritten.setId(toOverwrite.getId());
		overwritten.setPrices(toOverwrite.getPrices());
		return overwritten;
	}
}
