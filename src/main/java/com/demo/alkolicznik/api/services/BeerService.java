package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.beer.*;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.PropertiesMissingException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BeerService {

    private BeerRepository beerRepository;

    private StoreRepository storeRepository;

    private BeerImageService imageService;

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

    public List<BeerResponseDTO> getBeers() {
        return BeerResponseDTO.asList(beerRepository.findAllByOrderByIdAsc());
    }

    public BeerResponseDTO add(BeerRequestDTO requestDTO) {
        Beer beer = BeerRequestDTO.toModel(requestDTO);
        if (beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        return new BeerResponseDTO(beerRepository.save(beer));
    }

    public BeerResponseDTO replace(Long beerId, BeerRequestDTO requestDTO) {
        Beer toOverwrite = checkForPutConditions(beerId, requestDTO);
        Beer newBeer = BeerRequestDTO.toModel(requestDTO);
        Beer overwritten = updateFieldsOnPut(toOverwrite, newBeer);
        if (beerRepository.exists(overwritten)) throw new BeerAlreadyExistsException();
        // for each PUT request all the previous
        // beer prices for this beer MUST be deleted
        toOverwrite.deleteAllPrices();
        // for each PUT request the previous image MUST be deleted
        if (toOverwrite.getImage().isPresent()) {
            imageService.delete(toOverwrite.getImage().get());
        }
        return new BeerResponseDTO(beerRepository.save(toOverwrite));
    }

    public BeerResponseDTO update(Long beerId, BeerUpdateDTO updateDTO) {
        Beer beer = checkForPatchConditions(beerId, updateDTO);
        Beer original = (Beer) beer.clone();
        updateFieldsOnPatch(beer, updateDTO);

        if (beerRepository.exists(beer))
            throw new BeerAlreadyExistsException();
        // deleting prices on conditions
        if (this.pricesToDelete(updateDTO)) {
            beer.deleteAllPrices();
        }
        // deleting image on conditions
        if (this.imageToDelete(original, updateDTO)) {
            imageService.delete(beer.getImage().get());
        }
        return new BeerResponseDTO(beerRepository.save(beer));
    }

    public BeerDeleteResponseDTO delete(Long beerId) {
        Beer toDelete = beerRepository.findById(beerId).orElseThrow(() ->
                new BeerNotFoundException(beerId));
        beerRepository.delete(toDelete);
        if (toDelete.getImage().isPresent()) imageService.delete(toDelete.getImage().get());
        return new BeerDeleteResponseDTO(toDelete);
    }

    public BeerDeleteResponseDTO delete(BeerDeleteRequestDTO request) {
        Beer toDelete = beerRepository.findByFullnameAndVolume(request.getFullName(), request.getVolume())
                .orElseThrow(() -> new BeerNotFoundException(request.getFullName(), request.getVolume()));
        return this.delete(toDelete.getId());
    }

    private void updateFieldsOnPatch(Beer toUpdate, BeerUpdateDTO updateDTO) {
        String updatedBrand = updateDTO.getBrand();
        String updatedType = updateDTO.getType();
        Double updatedVolume = updateDTO.getVolume();

        if (updatedBrand != null) {
            toUpdate.setBrand(updatedBrand);
        }
        if (updatedType != null) {
            if (updatedType.isBlank()) toUpdate.setType(null);
            else toUpdate.setType(updatedType);
        }
        if (updatedVolume != null) {
            toUpdate.setVolume(updatedVolume);
        }
    }

    private Beer updateFieldsOnPut(Beer toOverwrite, Beer newBeer) {
        toOverwrite.setBrand(newBeer.getBrand());
        toOverwrite.setType(newBeer.getType());
        toOverwrite.setVolume(newBeer.getVolume());
        return toOverwrite;
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

    /**
     * If updated field is ONLY volume, then do not delete image
     */
    private boolean imageToDelete(Beer beforeUpdate, BeerUpdateDTO updateDTO) {
        if (beforeUpdate.getImage().isEmpty()) return false;
        if (updateDTO.getBrand() != null && !Objects.equals(beforeUpdate.getBrand(), updateDTO.getBrand())) {
            return true;
        }
        if (updateDTO.getType() != null && !Objects.equals(beforeUpdate.getType(), updateDTO.getType())) {
            return true;
        }
        return false;
    }

    private boolean pricesToDelete(BeerUpdateDTO updateDTO) {
        return updateDTO.getBrand() != null || updateDTO.getType() != null;
    }
}
