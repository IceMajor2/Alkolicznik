package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.NoSuchCityException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BeerService {

    private BeerRepository beerRepository;
    private StoreRepository storeRepository;

    public BeerService(BeerRepository beerRepository, StoreRepository storeRepository) {
        this.beerRepository = beerRepository;
        this.storeRepository = storeRepository;
    }

    public Beer get(Long beerId) {
        Optional<Beer> optBeer = beerRepository.findById(beerId);
        Beer beer = optBeer.orElseThrow(() -> new BeerNotFoundException(beerId));
        return beer;
    }

    public List<Beer> getBeers(String city) {
        if(!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException();
        }
        List<Store> cityStores = storeRepository.findAllByCity(city);

        List<Beer> beersInCity = new ArrayList<>();
        for(Store store : cityStores) {
            beersInCity.addAll(
                    store.getPrices().stream()
                            .map(BeerPrice::getBeer)
                            .collect(Collectors.toList())
            );
        }
        return beersInCity;
    }

    public List<BeerPrice> getBeerPrices() {
        List<Store> stores = storeRepository.findAll();
        List<BeerPrice> prices = new ArrayList<>();
        for(Store store : stores) {
            prices.addAll(store.getPrices());
        }
        return prices;
    }

    public List<BeerPrice> getBeerPrices(String city) {
        List<Store> cityStores = storeRepository.findAllByCity(city);

        if(cityStores.isEmpty()) {
            throw new NoSuchCityException();
        }

        List<BeerPrice> prices = new ArrayList<>();
        for(Store store : cityStores) {
            prices.addAll(store.getPrices());
        }
        return prices;
    }

    public List<BeerPrice> getBeerPrices(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        List<Store> stores = storeRepository.findAll();

        List<BeerPrice> prices = new ArrayList<>();
        for(Store store : stores) {
            store.getBeer(beerId).ifPresent((beerPrice -> prices.add(beerPrice)));
        }
        return prices;
    }

    public List<Beer> getBeers() {
        return beerRepository.findAll();
    }

    public Beer add(BeerRequestDTO beerRequestDTO) {
        Beer beer = beerRequestDTO.convertToModel();
        if(beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        return beerRepository.save(beer);
    }
}
