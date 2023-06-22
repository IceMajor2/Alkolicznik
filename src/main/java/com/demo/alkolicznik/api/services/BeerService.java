package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.repositories.BeerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeerService {

    private BeerRepository beerRepository;

    public BeerService(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    public Beer get(Long beerId) {
        Optional<Beer> optBeer = beerRepository.findById(beerId);
        if(optBeer.isEmpty()) {
            return null;
        }
        return optBeer.get();
    }

    public List<Beer> getBeers() {
        return beerRepository.findAll();
    }

    public Beer add(Beer beer) {
        return beerRepository.save(beer);
    }
}
