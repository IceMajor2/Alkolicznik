package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.beer.BeerDeleteDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.ImageModel;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.vaadin.flow.component.html.Image;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<BeerResponseDTO> getBeers(String city) {
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);

        Set<Beer> beersInCity = new TreeSet<>(Comparator.comparing(Beer::getId));
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
        return this.mapToDto(beerRepository.findAllByOrderByIdAsc());
    }

    // TODO: probably should be a transaction
    public BeerResponseDTO add(BeerRequestDTO requestDTO) {
        Beer beer = requestDTO.convertToModelNoImage();

        if (beerRepository.exists(beer)) {
            throw new BeerAlreadyExistsException();
        }
        // After beer's validation, we can begin (if passed)
        // the uploading of image and attaching it to beer object.
        String imagePath = requestDTO.getImagePath();
        if (imagePath != null) {
            addImage(beer, imagePath);
        }
        return new BeerResponseDTO(beerRepository.save(beer));
    }

    public BeerResponseDTO replace(Long beerId, BeerRequestDTO requestDTO) {
        Beer toOverwrite = checkForPutConditions(beerId, requestDTO);
        Beer newBeer = requestDTO.convertToModelNoImage();

        updateFieldsOnPut(toOverwrite, newBeer);

        // for each PUT request the previous image MUST be deleted/replaced
        if (toOverwrite.getImage().isPresent()) {
            imageService.deleteBeerImage(toOverwrite);
        }
        // after deleting previous image, check if there is a replacement
        // if yes, execute the wole 'addImage' procedure
        if (requestDTO.getImagePath() != null) {
            addImage(toOverwrite, requestDTO.getImagePath());
        }
        // for each PUT request all the previous
        // beer prices for this beer MUST be deleted
        toOverwrite.deletePrices();
        return new BeerResponseDTO(beerRepository.save(toOverwrite));
    }

    public BeerResponseDTO update(Long beerId, BeerUpdateDTO updateDTO) {
        Beer beer = checkForPatchConditions(beerId, updateDTO);
        updateFieldsOnPatch(beer, updateDTO);

        // updating an image (if present)...
        String imagePath = updateDTO.getImagePath();
        if (imagePath != null) {
            if(beer.getImage().isPresent()) imageService.deleteBeerImage(beer);
            ImageModel imageModel = imageService.upload(imagePath,
                    imageService.createImageFilename(beer, imageService.extractFileExtensionFromPath(imagePath)));
            //imageService.deleteBeerImage(beer);
            beer.setImage(imageModel);
            imageModel.setBeer(beer);
            imageService.save(imageModel);
        }
        // ...or deleting if the brand / type was changed
        else if (updateDTO.imageToDelete()) {
            imageService.deleteBeerImage(beer);
        }
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

    public Image getImageComponent(Long beerId) {
        return imageService.getVaadinBeerImage(beerId);
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

    private void updateFieldsOnPut(Beer toOverwrite, Beer newBeer) {
        toOverwrite.setBrand(newBeer.getBrand());
        toOverwrite.setType(newBeer.getType());
        toOverwrite.setVolume(newBeer.getVolume());
    }

    private List<BeerResponseDTO> mapToDto(Collection<Beer> beers) {
        return beers.stream()
                .map(BeerResponseDTO::new)
                .toList();
    }

    private Beer checkForUpdateConditions(Long beerId, BeerUpdateDTO updateDTO) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (!updateDTO.anythingToUpdate(beer)) {
            throw new ObjectsAreEqualException();
        }
        Beer converted = updateDTO.convertToModelNoImage();
        if (converted != null && beerRepository.exists(converted)) {
            throw new BeerAlreadyExistsException();
        }
        return beer;
    }

    private Beer checkForPatchConditions(Long beerId, BeerUpdateDTO updateDTO) {
        if(updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        return checkForUpdateConditions(beerId, updateDTO);
    }

    private Beer checkForPutConditions(Long beerId, BeerRequestDTO updateDTO) {
        return checkForUpdateConditions(beerId, new BeerUpdateDTO(updateDTO));
    }

    private void addImage(Beer beer, String imagePath) {
        ImageModel imageModel = imageService.upload(imagePath,
                imageService.createImageFilename(beer, imageService.extractFileExtensionFromPath(imagePath)));
        beer.setImage(imageModel);
        imageModel.setBeer(beer);
        imageService.save(imageModel);
    }
}
