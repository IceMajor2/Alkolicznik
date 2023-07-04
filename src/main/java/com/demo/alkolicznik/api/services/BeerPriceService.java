package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class BeerPriceService {

    private StoreRepository storeRepository;
    private BeerRepository beerRepository;

    public BeerPriceService(StoreRepository storeRepository, BeerRepository beerRepository) {
        this.storeRepository = storeRepository;
        this.beerRepository = beerRepository;
    }

    public BeerPrice add(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
        // Fetch both store and beer from repositories.
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        // Check if beer exists by fullname
        String beerFullname = beerPriceRequestDTO.getBeerName();
        if (!beerRepository.existsByFullname(beerFullname)) {
            throw new BeerNotFoundException(beerFullname);
        }
        double volume = beerPriceRequestDTO.getBeerVolume();
        // If beer is not found in DB, then the reason is volume
        Beer beer = beerRepository.findByFullnameAndVolume(beerFullname, volume).orElseThrow(
                () -> new BeerNotFoundException(beerFullname, volume)
        );
        if (store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        // Pass beer with price to store and save changes.
        double price = beerPriceRequestDTO.getPrice();
        store.addBeer(beer, price);
        storeRepository.save(store);
        // Convert to and return BeerPriceResponseDTO.
        return store.getBeer(beerFullname).get();
    }

    public BeerPrice add(Long storeId, Long beerId, double price) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        store.addBeer(beer, price);
        storeRepository.save(store);
        return store.getBeer(beerId).get();
    }

    public Set<BeerPrice> getBeerPricesOnStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        Set<BeerPrice> beers = store.getPrices();
        return beers;
    }

    public BeerPrice get(Long storeId, Long beerId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        return store.getBeer(beerId).orElseThrow(
                () -> new BeerPriceNotFoundException()
        );
    }

    public Set<BeerPrice> getBeerPrices() {
        List<Store> stores = storeRepository.findAll();
        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : stores) {
            prices.addAll(store.getPrices());
        }
        return prices;
    }

    public Set<BeerPrice> getBeerPrices(String city) {
        List<Store> cityStores = storeRepository.findAllByCity(city);

        if (cityStores.isEmpty()) {
            throw new NoSuchCityException(city);
        }

        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : cityStores) {
            prices.addAll(store.getPrices());
        }
        return prices;
    }

    public Set<BeerPrice> getBeerPricesOnBeerId(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        List<Store> stores = storeRepository.findAll();

        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : stores) {
            store.getBeer(beerId).ifPresent((beerPrice -> prices.add(beerPrice)));
        }
        return prices;
    }

    public Set<BeerPrice> getBeerPrices(Long beerId, String city) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if(!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        Set<BeerPrice> beerPricesInCity = new LinkedHashSet<>();
        for(BeerPrice beerPrice: beer.getPrices()) {
            if(beerPrice.getStore().getCity().equals(city)) {
                beerPricesInCity.add(beerPrice);
            }
        }
        return beerPricesInCity;
    }
}
