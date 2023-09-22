package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.beer.*;
import com.demo.alkolicznik.exceptions.classes.city.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.PropertiesMissingException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerService {

    private final BeerRepository beerRepository;
    private final StoreRepository storeRepository;
    private final BeerImageService imageService;

    @Transactional(readOnly = true)
    public BeerResponseDTO get(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        return new BeerResponseDTO(beer);
    }

    @Transactional(readOnly = true)
    public List<BeerResponseDTO> getBeers(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);
        // order by id ascending
        Set<Beer> beersInCity = new TreeSet<>(Comparator.comparing(Beer::getId));
        for (Store store : cityStores) {
            beersInCity.addAll(
                    store.getPrices().stream()
                            .map(BeerPrice::getBeer)
                            .toList()
            );
        }
        return BeerResponseDTO.asList(beersInCity);
    }

    @Transactional(readOnly = true)
    public List<BeerResponseDTO> getBeers() {
        return BeerResponseDTO.asList(beerRepository.findAllByOrderByIdAsc());
    }

    @Transactional(readOnly = false)
    public BeerResponseDTO add(BeerRequestDTO requestDTO) {
        Beer beer = BeerRequestDTO.toModel(requestDTO);
        if (beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        BeerResponseDTO saved = new BeerResponseDTO(beerRepository.save(beer));
        log.info("Added: [{}]", saved);
        return saved;
    }

    public BeerResponseDTO replace(Long beerId, BeerRequestDTO requestDTO) {
        // CONDITIONS: start
        Beer toOverwrite = checkForPutConditions(beerId, requestDTO);
        Beer overwritten = BeerRequestDTO.toOverwrittenModel(requestDTO, toOverwrite);
        if (beerRepository.exists(overwritten))
            throw new BeerAlreadyExistsException();
        // CONDITIONS: end
        overwritten.deleteAllPrices();
        toOverwrite.getImage().ifPresent(beerImage -> {
            imageService.delete(beerImage);
            overwritten.setImage(null);
        });
        BeerResponseDTO previous = new BeerResponseDTO(toOverwrite);
        BeerResponseDTO saved = new BeerResponseDTO(beerRepository.save(overwritten));
        log.info("Replacing: [{}] with: [{}]", previous, saved);
        return saved;
    }

    public BeerResponseDTO update(Long beerId, BeerUpdateDTO updateDTO) {
        // CONDITIONS: start
        Beer toUpdate = checkForPatchConditions(beerId, updateDTO);
        Beer updated = BeerUpdateDTO.toModel(updateDTO, toUpdate);
        if (beerRepository.exists(updated))
            throw new BeerAlreadyExistsException();
        // CONDITIONS: end
        if (this.pricesToDelete(toUpdate, updateDTO)) {
            // updating 'BRAND' or 'TYPE' should make all prices be deleted
            updated.deleteAllPrices();
            // same logic applies to image removal, but it additionally needs to exist first
            if (toUpdate.getImage().isPresent()) {
                imageService.delete(toUpdate.getImage().get());
                updated.setImage(null);
            }
        }
        BeerResponseDTO previous = new BeerResponseDTO(toUpdate);
        BeerResponseDTO saved = new BeerResponseDTO(beerRepository.save(updated));
        log.info("Updating: [{}] to: [{}]", previous, saved);
        return saved;
    }

    @Transactional(readOnly = false)
    public BeerDeleteDTO delete(Long beerId) {
        Beer toDelete = beerRepository.findById(beerId).orElseThrow(() ->
                new BeerNotFoundException(beerId));
        return this.delete(toDelete);
    }

    @Transactional(readOnly = false)
    public BeerDeleteDTO delete(Beer beer) {
        BeerDeleteDTO deleted = new BeerDeleteDTO(beer);
        beerRepository.delete(beer);
        if (beer.getImage().isPresent())
            imageService.delete(beer.getImage().get());
        log.info("Deleted: [{}]", deleted);
        return deleted;
    }

    @Transactional(readOnly = false)
    public BeerDeleteDTO delete(BeerRequestDTO request) {
        Beer toFind = BeerRequestDTO.toModel(request);
        Beer toDelete = beerRepository.findByFullnameAndVolume(toFind.getFullName(), toFind.getVolume())
                .orElseThrow(() -> new BeerNotFoundException(toFind.getFullName(), toFind.getVolume()));
        return this.delete(toDelete);
    }

    private Beer checkForUpdateConditions(Long beerId, BeerUpdateDTO updateDTO) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (!updateDTO.anythingToUpdate(beer)) {
            throw new ObjectsAreEqualException();
        }
        return beer;
    }

    private Beer checkForPatchConditions(Long beerId, BeerUpdateDTO updateDTO) {
        if (updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        return checkForUpdateConditions(beerId, updateDTO);
    }

    private Beer checkForPutConditions(Long beerId, BeerRequestDTO requestDTO) {
        return checkForUpdateConditions(beerId, new BeerUpdateDTO(requestDTO));
    }

    private boolean pricesToDelete(Beer beforeUpdate, BeerUpdateDTO updateDTO) {
        return (updateDTO.getBrand() != null && !Objects.equals(beforeUpdate.getBrand(), updateDTO.getBrand()))
                || updateDTO.getType() != null && !Objects.equals(beforeUpdate.getType(), updateDTO.getType());
    }
}
