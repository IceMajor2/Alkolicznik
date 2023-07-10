package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BeerService {

    private BeerRepository beerRepository;
    private StoreRepository storeRepository;

    public BeerService(BeerRepository beerRepository, StoreRepository storeRepository) {
        this.beerRepository = beerRepository;
        this.storeRepository = storeRepository;
    }

    public BeerResponseDTO get(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        return new BeerResponseDTO(beer);
    }

    public List<BeerResponseDTO> getBeers(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        List<Store> cityStores = storeRepository.findAllByCity(city);

        List<Beer> beersInCity = new ArrayList<>();
        for (Store store : cityStores) {
            beersInCity.addAll(
                    store.getPrices().stream()
                            .map(BeerPrice::getBeer)
                            .toList()
            );
        }
        return this.mapToDto(beersInCity);
    }

    public List<BeerResponseDTO> getBeers() {
        return this.mapToDto(beerRepository.findAll());
    }

    public BeerResponseDTO add(BeerRequestDTO beerRequestDTO) {
        Beer beer = beerRequestDTO.convertToModel();
        if (beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        return new BeerResponseDTO(beerRepository.save(beer));
    }

    public BeerResponseDTO update(Long beerId, BeerUpdateDTO updateDTO) {
        if (updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (!updateDTO.anythingToUpdate(beer)) {
            throw new ObjectsAreEqualException();
        }
        setFieldsWhenValidated(beer, updateDTO);
        return new BeerResponseDTO(beerRepository.save(beer));
    }

    public BeerDeleteDTO delete(Long beerId) {
        Beer toDelete = beerRepository.findById(beerId).orElseThrow(() ->
                new BeerNotFoundException(beerId));
        beerRepository.delete(toDelete);
        return new BeerDeleteDTO(toDelete);
    }

    private void setFieldsWhenValidated(Beer beer, BeerUpdateDTO updateDTO) {
        String updatedBrand = updateDTO.getBrand();
        String updatedType = updateDTO.getType();
        Double updatedVolume = updateDTO.getVolume();

        if (updatedBrand != null) {
            beer.setBrand(updatedBrand);
        }
        if (updatedType != null) {
            if ("".equals(updatedType)) {
                beer.setType(null);
            } else {
                beer.setType(updatedType);
            }
        }
        if (updatedVolume != null) {
            beer.setVolume(updatedVolume);
        }
    }

    private List<BeerResponseDTO> mapToDto(List<Beer> beers) {
        return beers.stream()
                .map(BeerResponseDTO::new)
                .toList();
    }
}
