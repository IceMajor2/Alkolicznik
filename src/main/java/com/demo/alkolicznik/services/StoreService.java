package com.demo.alkolicznik.services;

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

    public List<Store> getStores() {
        return storeRepository.findAll();
    }
}
