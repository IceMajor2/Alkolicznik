package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return this.mapToDto(storeRepository.findAllByCity(city));
    }

    public List<StoreResponseDTO> getStores() {
        return this.mapToDto(storeRepository.findAll());
    }

    public StoreResponseDTO add(StoreRequestDTO storeRequestDTO) {
        Store store = storeRequestDTO.convertToModel();
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

    public StoreResponseDTO update(Long storeId, StoreUpdateDTO updateDTO) {
        if (updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        if (!updateDTO.anythingToUpdate(store)) {
            throw new ObjectsAreEqualException();
        }
        String updatedName = updateDTO.getName();
        String updatedCity = updateDTO.getCity();
        String updatedStreet = updateDTO.getStreet();
        if (updatedName != null) {
            store.setName(updatedName);
        }
        if (updatedCity != null) {
            store.setCity(updatedCity);
        }
        if (updatedStreet != null) {
            store.setStreet(updatedStreet);
        }
        return new StoreResponseDTO(storeRepository.save(store));
    }

    public StoreDeleteDTO delete(Long storeId) {
        Store toDelete = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        storeRepository.delete(toDelete);
        return new StoreDeleteDTO(toDelete);
    }

    private List<StoreResponseDTO> mapToDto(List<Store> stores) {
        return stores.stream()
                .map(StoreResponseDTO::new)
                .toList();
    }
}
