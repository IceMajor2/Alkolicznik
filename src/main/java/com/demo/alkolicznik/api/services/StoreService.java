package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.city.CityDTO;
import com.demo.alkolicznik.dto.store.*;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.PropertiesMissingException;
import com.demo.alkolicznik.exceptions.classes.store.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreImageService imageService;

    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getStores(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        return StoreResponseDTO.asList(storeRepository.findAllByCityOrderByIdAsc(city));
    }

    @Transactional(readOnly = true)
    public List<StoreResponseDTO> getStores() {
        return StoreResponseDTO.asList(storeRepository.findAllByOrderByIdAsc());
    }

    @Transactional(readOnly = true)
    public StoreResponseDTO get(Long storeId) {
        return new StoreResponseDTO(storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId))
        );
    }

    @Transactional(readOnly = true)
    public List<StoreNameDTO> getAllBrands() {
        return StoreNameDTO.asList(storeRepository.findDistinctNames());
    }

    @Transactional(readOnly = true)
    public List<CityDTO> getAllCities() {
        return CityDTO.asList(storeRepository.findDistinctCities());
    }

    @Transactional(readOnly = false)
    public StoreResponseDTO add(StoreRequestDTO requestDTO) {
        Optional<StoreImage> optImage = imageService.findByStoreName(requestDTO.getName());
        Store store = StoreRequestDTO.toModel(requestDTO, optImage);
        if (storeRepository.exists(store)) throw new StoreAlreadyExistsException();
        return new StoreResponseDTO(storeRepository.save(store));
    }

    @Transactional(readOnly = false)
    public StoreResponseDTO replace(Long storeId, StoreRequestDTO requestDTO) {
        // CONDITIONS: start
        Store toOverwrite = checkForPutConditions(storeId, requestDTO);
        Store overwritten = createOverwrittenModel(requestDTO, toOverwrite);
        if (storeRepository.exists(overwritten)) {
            throw new StoreAlreadyExistsException();
        }
        // CONDITIONS: end
        overwritten.deleteAllPrices();

        if (isPreviousImageToDelete(toOverwrite, overwritten))
            imageService.delete(toOverwrite.getImage().get());
        return new StoreResponseDTO(storeRepository.save(overwritten));
    }

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public StoreResponseDTO update(Long storeId, StoreUpdateDTO updateDTO) {
        // CONDITIONS: start
        Store store = checkForPatchConditions(storeId, updateDTO);
        Store updated = StoreUpdateDTO.toModel(updateDTO, store);
        if (storeRepository.exists(updated)) {
            throw new StoreAlreadyExistsException();
        }
        // CONDITIONS: end
        updated.deleteAllPrices();

        if (isPreviousImageToDelete(store, updated))
            imageService.delete(store.getImage().get());
        return new StoreResponseDTO(storeRepository.save(updated));
    }

    @Transactional(readOnly = false)
    public StoreDeleteDTO delete(Long storeId) {
        Store toDelete = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));

        toDelete.deleteAllPrices();
        if (storeRepository.isNameUnique(toDelete.getName()))
            toDelete.getImage().ifPresent(storeImage -> imageService.delete(storeImage.getStoreName()));

        storeRepository.delete(toDelete);
        return new StoreDeleteDTO(toDelete);
    }

    @Transactional(readOnly = false)
    public StoreDeleteDTO delete(StoreRequestDTO store) {
        Store toDelete = storeRepository.find(StoreRequestDTO.toModel(store, Optional.empty()))
                .orElseThrow(() -> new StoreNotFoundException
                        (store.getName(), store.getCity(), store.getStreet()));
        return this.delete(toDelete.getId());
    }

    private boolean isPreviousImageToDelete(Store prevStore, Store newStore) {
        return prevStore.getImage().isPresent()
                && !Objects.equals(prevStore.getName(), newStore.getName())
                && storeRepository.isNameUnique(prevStore.getName());
    }

    private Store checkForPatchConditions(Long storeId, StoreUpdateDTO updateDTO) {
        if (updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        return checkForUpdateConditions(storeId, updateDTO);
    }

    private Store checkForPutConditions(Long storeId, StoreRequestDTO requestDTO) {
        return checkForUpdateConditions(storeId, new StoreUpdateDTO(requestDTO));
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

    private Store createOverwrittenModel(StoreRequestDTO requestDTO, Store toOverwrite) {
        Optional<StoreImage> image = imageService.findByStoreName(requestDTO.getName());
        Store overwritten = StoreRequestDTO.toModel(requestDTO, image);
        overwritten.setId(toOverwrite.getId());
        overwritten.setPrices(toOverwrite.getPrices());
        return overwritten;
    }
}
