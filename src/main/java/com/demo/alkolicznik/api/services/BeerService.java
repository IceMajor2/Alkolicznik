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

    public List<BeerPrice> getBeers(String city) {
        List<Store> cityStores = storeRepository.findAllByCity(city);

        if(cityStores.isEmpty()) {
            throw new NoSuchCityException();
        }

        List<BeerPrice> beers = new ArrayList<>();
        for(Store store : cityStores) {
            beers.addAll(store.getPrices());
        }
        return beers;
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

    public BeerResponseDTO convertToResponseDto(Beer beer) {
        return new BeerResponseDTO(beer);
    }
}
