package com.demo.alkolicznik.services;

import com.demo.alkolicznik.dto.BeerPriceDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private StoreRepository storeRepository;

    private BeerRepository beerRepository;

    public StoreService(StoreRepository storeRepository, BeerRepository beerRepository) {
        this.storeRepository = storeRepository;
        this.beerRepository = beerRepository;
    }

    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    public Store add(Store store) {
        if(storeRepository.existsByName(store.getName())) {
            return null;
        }
        return storeRepository.save(store);
    }

    public Store get(Long id) {
        Optional<Store> optStore = storeRepository.findById(id);
        if(optStore.isEmpty()) {
            return null;
        }
        return optStore.get();
    }

    public BeerPrice addBeer(Long storeId, BeerPriceDTO beerPriceDTO) {
        // Fetch both store and beer from repositories.
        Store store = storeRepository.findById(storeId).get();
        String beerName = beerPriceDTO.getBeer();
        Beer beer = beerRepository.findByName(beerName).get();
        // Pass beer with price to store and save changes.
        double price = beerPriceDTO.getPrice();
        store.addBeer(beer, price);
        storeRepository.save(store);
        // Return saved BeerPrice.
        return store.getBeer(beerName).get();
    }
}
