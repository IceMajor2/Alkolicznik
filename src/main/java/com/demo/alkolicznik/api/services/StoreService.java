package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.StoreRequestDTO;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.StoreNotFoundException;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Store get(Long id) {
        Optional<Store> optStore = storeRepository.findById(id);
        Store store = optStore.orElseThrow(() -> new StoreNotFoundException(id));
        return store;
    }
}
