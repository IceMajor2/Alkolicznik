package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.BeerAlreadyExists;
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

    public Beer add(BeerRequestDTO beerRequestDTO) {
        Beer beer = beerRequestDTO.convertToModel();
        if(beerRepository.exists(beer)) {
            throw new BeerAlreadyExists();
        }
        return beerRepository.save(beer);
    }

    public BeerResponseDTO convertToResponseDto(Beer beer) {
        return new BeerResponseDTO(beer);
    }
}
