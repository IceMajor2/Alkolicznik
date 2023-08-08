package com.demo.alkolicznik.api.services;

import java.util.List;

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
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.ModelDtoConverter;

import org.springframework.stereotype.Service;

import static com.demo.alkolicznik.utils.ModelDtoConverter.storeListToDtoList;

@Service
public class StoreService {

	private StoreRepository storeRepository;

	public StoreService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	public List<StoreResponseDTO> getStores(String city) {
		if (!storeRepository.existsByCity(city)) {
			throw new NoSuchCityException(city);
		}
		return storeListToDtoList(storeRepository.findAllByCityOrderByIdAsc(city));
	}

	public List<StoreResponseDTO> getStores() {
		return storeListToDtoList(storeRepository.findAllByOrderByIdAsc());
	}

	public StoreResponseDTO add(StoreRequestDTO requestDTO) {
		Store store = ModelDtoConverter.convertToModelNoImage(requestDTO);
		if (storeRepository.existsByNameAndCityAndStreet(store.getName(), store.getCity(), store.getStreet())) {
			throw new StoreAlreadyExistsException();
		}
		return new StoreResponseDTO(storeRepository.save(store));
	}

	public StoreResponseDTO get(Long storeId) {
		return new StoreResponseDTO(storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId))
		);
	}

	public StoreResponseDTO replace(Long storeId, StoreRequestDTO requestDTO) {
		Store toOverwrite = checkForPutConditions(storeId, requestDTO);
		Store newStore = ModelDtoConverter.convertToModelNoImage(requestDTO);
		Store overwritten = updateFieldsOnPut(toOverwrite, newStore);

		if (storeRepository.exists(overwritten)) {
			throw new StoreAlreadyExistsException();
		}

		// for each PUT request all the previous
		// beer prices in this store MUST be deleted
		overwritten.deleteAllPrices();
		return new StoreResponseDTO(storeRepository.save(toOverwrite));
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

	private Store updateFieldsOnPut(Store toOverwrite, Store newStore) {
		toOverwrite.setName(newStore.getName());
		toOverwrite.setCity(newStore.getCity());
		toOverwrite.setStreet(newStore.getStreet());
		return toOverwrite;
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
