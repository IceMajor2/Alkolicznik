package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.StoreRequestDTO;
import com.demo.alkolicznik.exceptions.*;
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

    public BeerPrice addBeer(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
        // Fetch both store and beer from repositories.
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        // Check if beer exists by fullname
        String beerFullname = beerPriceRequestDTO.getBeerName();
        if(!beerRepository.existsByFullname(beerFullname)) {
            throw new BeerNotFoundException(beerFullname);
        }
        double volume = beerPriceRequestDTO.getBeerVolume();
        // If beer is not found in DB, then the reason is volume
        Beer beer = beerRepository.findByFullnameAndVolume(beerFullname, volume).orElseThrow(
                () -> new BeerNotFoundException(volume)
        );
        if(store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        // Pass beer with price to store and save changes.
        double price = beerPriceRequestDTO.getPrice();
        store.addBeer(beer, price);
        storeRepository.save(store);
        // Convert to and return BeerPriceResponseDTO.
        return store.getBeer(beerFullname).get();
    }

    public BeerPrice addBeer(Long storeId, Long beerId, double price) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if(store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        store.addBeer(beer, price);
        storeRepository.save(store);
        return store.getBeer(beerId).get();
    }

    public Set<BeerPrice> getBeers(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        Set<BeerPrice> beers = store.getPrices();
        return beers;
    }
}
