package com.demo.alkolicznik.services;

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

    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    public Store add(Store store) {
        return storeRepository.save(store);
    }

    public Store get(Long id) {
        Optional<Store> optStore = storeRepository.findById(id);
        if(optStore.isEmpty()) {
            return null;
        }
        return optStore.get();
    }
}
