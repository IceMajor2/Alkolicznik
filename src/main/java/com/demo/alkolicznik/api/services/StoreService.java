package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.StoreRequestDTO;
import com.demo.alkolicznik.exceptions.StoreAlreadyExistsException;
import com.demo.alkolicznik.exceptions.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StoreService {

    private StoreRepository storeRepository;
    private BeerRepository beerRepository;

    public StoreService(StoreRepository storeRepository, BeerRepository beerRepository) {
        this.storeRepository = storeRepository;
        this.beerRepository = beerRepository;
    }

    public List<Store> getStores(String city) {
        return storeRepository.findAllByCity(city);
    }

    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    public Store add(StoreRequestDTO storeRequestDTO) {
        Store store = storeRequestDTO.convertToModel();
        if(storeRepository.existsByNameAndCityAndStreet(store.getName(), store.getCity(), store.getStreet())) {
            throw new StoreAlreadyExistsException();
        }
        return storeRepository.save(store);
    }

    public Store get(Long id) {
        Optional<Store> optStore = storeRepository.findById(id);
        Store store = optStore.orElseThrow(() -> new StoreNotFoundException(id));
        return store;
    }

    public BeerPriceResponseDTO addBeer(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
        // Fetch both store and beer from repositories.
        Store store = storeRepository.findById(storeId).get();
        String beerFullname = beerPriceRequestDTO.getBeerName();
        Beer beer = beerRepository.findByFullname(beerFullname).get();
        // Pass beer with price to store and save changes.
        double price = beerPriceRequestDTO.getPrice();
        store.addBeer(beer, price);
        storeRepository.save(store);
        // Convert to and return BeerPriceResponseDTO.
        return new BeerPriceResponseDTO(store.getBeer(beerFullname).get());
    }

    public Set<BeerPrice> getBeers(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        Set<BeerPrice> beers = store.getPrices();
        return beers;
    }
}
