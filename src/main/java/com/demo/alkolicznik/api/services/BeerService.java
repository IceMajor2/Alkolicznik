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
import com.vaadin.flow.component.html.Image;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BeerService {

    private BeerRepository beerRepository;
    private StoreRepository storeRepository;
    private ImageService imageService;

    public BeerService(BeerRepository beerRepository, StoreRepository storeRepository, ImageService imageService) {
        this.beerRepository = beerRepository;
        this.storeRepository = storeRepository;
        this.imageService = imageService;
    }

    public BeerResponseDTO get(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        return new BeerResponseDTO(beer);
    }

    public Image getImageComponent(Long beerId) {
        return imageService.getBeerImageComponent(beerId);
    }

    public List<BeerResponseDTO> getBeers(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);

        List<Beer> beersInCity = new ArrayList<>();
        for (Store store : cityStores) {
            beersInCity.addAll(
                    store.getPrices().stream()
                            .map(BeerPrice::getBeer)
                            .toList()
            );
        }
        beersInCity.sort(Comparator.comparing(Beer::getId));
        return this.mapToDto(beersInCity);
    }

    public List<BeerResponseDTO> getBeers() {
        return this.mapToDto(beerRepository.findAllByOrderByIdAsc());
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

    public BeerDeleteDTO delete(BeerRequestDTO beer) {
        Beer toDelete = beerRepository.findByFullnameAndVolume(beer.getFullName(), beer.getVolume())
                .orElseThrow(() -> new BeerNotFoundException(beer.getFullName(), beer.getVolume()));
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
