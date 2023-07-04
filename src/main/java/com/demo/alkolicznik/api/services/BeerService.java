package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.exceptions.classes.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        return beer;
    }

    public List<Beer> getBeers(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        List<Store> cityStores = storeRepository.findAllByCity(city);

        List<Beer> beersInCity = new ArrayList<>();
        for (Store store : cityStores) {
            beersInCity.addAll(
                    store.getPrices().stream()
                            .map(BeerPrice::getBeer)
                            .collect(Collectors.toList())
            );
        }
        return beersInCity;
    }

    public List<Beer> getBeers() {
        return beerRepository.findAll();
    }

    public Beer add(BeerRequestDTO beerRequestDTO) {
        Beer beer = beerRequestDTO.convertToModel();
        if (beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        return beerRepository.save(beer);
    }

    public Beer update(Long beerId, BeerUpdateDTO updateDTO) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        String updatedBrand = updateDTO.getBrand();
        String updatedType = updateDTO.getType();
        Double updatedVolume = updateDTO.getVolume();
        if(updatedBrand != null) {
            beer.setBrand(updatedBrand);
        }
        if(updatedType != null) {
            beer.setType(updatedType);
        }
        if(updatedVolume != null) {
            beer.setVolume(updatedVolume);
        }
        return beerRepository.save(beer);
    }
}
