package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.StoreRequestDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StoreService {

    private StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public List<Store> getStores(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        return storeRepository.findAllByCity(city);
    }

    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    public Store add(StoreRequestDTO storeRequestDTO) {
        Store store = storeRequestDTO.convertToModel();
        if (storeRepository.existsByNameAndCityAndStreet(store.getName(), store.getCity(), store.getStreet())) {
            throw new StoreAlreadyExistsException();
        }
        return storeRepository.save(store);
    }

    public Store get(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(() ->
                        new StoreNotFoundException(storeId));
    }

    public Store update(Long storeId, StoreUpdateDTO updateDTO) {
        if(updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        if(!updateDTO.anythingToUpdate(store)) {
            throw new ObjectsAreEqualException();
        }
        String updatedName = updateDTO.getName();
        String updatedCity = updateDTO.getCity();
        String updatedStreet = updateDTO.getStreet();
        if(updatedName != null) {
            store.setName(updatedName);
        }
        if(updatedCity != null) {
            store.setCity(updatedCity);
        }
        if(updatedStreet != null) {
            store.setStreet(updatedStreet);
        }
        return storeRepository.save(store);
    }

    public Store delete(Long storeId) {
        Store toDelete = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        storeRepository.delete(toDelete);
        return toDelete;
    }
}
