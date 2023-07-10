package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.delete.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.classes.*;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BeerPriceService {

    private StoreRepository storeRepository;
    private BeerRepository beerRepository;

    public BeerPriceService(StoreRepository storeRepository, BeerRepository beerRepository) {
        this.storeRepository = storeRepository;
        this.beerRepository = beerRepository;
    }

    public BeerPriceResponseDTO add(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
        // Fetch both store and beer from repositories.
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        // Check if beer exists by fullname
        String beerFullname = beerPriceRequestDTO.getBeerName();
        if (!beerRepository.existsByFullname(beerFullname)) {
            throw new BeerNotFoundException(beerFullname);
        }
        double volume = beerPriceRequestDTO.getBeerVolume();
        // If beer is not found in DB, then the reason is volume
        Beer beer = beerRepository.findByFullnameAndVolume(beerFullname, volume).orElseThrow(
                () -> new BeerNotFoundException(beerFullname, volume)
        );
        if (store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        // Pass beer with price to store and save changes.
        double price = beerPriceRequestDTO.getPrice();
        store.addBeer(beer, price);
        storeRepository.save(store);
        return new BeerPriceResponseDTO(store.getBeer(beerFullname).get());
    }

    public BeerPriceResponseDTO add(Long storeId, Long beerId, double price) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (store.getBeer(beer.getFullName()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        store.addBeer(beer, price);
        storeRepository.save(store);
        return new BeerPriceResponseDTO(store.getBeer(beerId).get());
    }

    public Set<BeerPriceResponseDTO> getBeerPricesOnStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        Set<BeerPrice> prices = store.getPrices();
        return this.mapToDto(prices);
    }

    public BeerPriceResponseDTO get(Long storeId, Long beerId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new StoreNotFoundException(storeId)
        );
        if (!beerRepository.existsById(beerId)) {
            throw new BeerNotFoundException(beerId);
        }
        return new BeerPriceResponseDTO(store.getBeer(beerId).orElseThrow(
                () -> new BeerPriceNotFoundException()
        ));
    }

    public Set<BeerPriceResponseDTO> getBeerPrices() {
        List<Store> stores = storeRepository.findAll();
        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : stores) {
            prices.addAll(store.getPrices());
        }

        Set<BeerPriceResponseDTO> pricesDTO = prices.stream()
                .map(BeerPriceResponseDTO::new)
                .collect(Collectors.toSet());
        return pricesDTO;
    }

    public Set<BeerPriceResponseDTO> getBeerPrices(String city) {
        List<Store> cityStores = storeRepository.findAllByCity(city);

        if (cityStores.isEmpty()) {
            throw new NoSuchCityException(city);
        }

        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : cityStores) {
            prices.addAll(store.getPrices());
        }
        return this.mapToDto(prices);
    }

    public Set<BeerPriceResponseDTO> getBeerPricesOnBeerId(Long beerId) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        List<Store> stores = storeRepository.findAll();

        Set<BeerPrice> prices = new LinkedHashSet<>();
        for (Store store : stores) {
            store.getBeer(beerId).ifPresent((beerPrice -> prices.add(beerPrice)));
        }
        return this.mapToDto(prices);
    }

    public Set<BeerPriceResponseDTO> getBeerPrices(Long beerId, String city) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(
                () -> new BeerNotFoundException(beerId)
        );
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        Set<BeerPrice> beerPricesInCity = new LinkedHashSet<>();
        for (BeerPrice beerPrice : beer.getPrices()) {
            if (beerPrice.getStore().getCity().equals(city)) {
                beerPricesInCity.add(beerPrice);
            }
        }
        return this.mapToDto(beerPricesInCity);
    }

    public BeerPriceResponseDTO update(Long storeId, Long beerId, BeerPriceUpdateDTO updateDTO) {
        if (updateDTO.propertiesMissing()) {
            throw new PropertiesMissingException();
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        if (!beerRepository.existsById(beerId)) {
            throw new BeerNotFoundException(beerId);
        }
        BeerPrice beerPrice = store.getBeer(beerId).orElseThrow(() ->
                new BeerPriceNotFoundException());
        if (!updateDTO.anythingToUpdate(beerPrice)) {
            throw new ObjectsAreEqualException();
        }

        MonetaryAmount updatedPrice = Monetary.getDefaultAmountFactory()
                .setCurrency("PLN").setNumber(updateDTO.getPrice()).create();
        beerPrice.setPrice(updatedPrice);

        storeRepository.save(store);
        return new BeerPriceResponseDTO(beerPrice);
    }

    public BeerPriceDeleteDTO delete(Long storeId, Long beerId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() ->
                new StoreNotFoundException(storeId));
        Beer beer = beerRepository.findById(beerId).orElseThrow(() ->
                new BeerNotFoundException(beerId));
        BeerPrice beerPrice = store.getBeer(beerId).orElseThrow(() ->
                new BeerPriceNotFoundException());
        BeerPrice deleted = store.removeBeer(beer);
        beerRepository.save(beer);
        storeRepository.save(store);
        return new BeerPriceDeleteDTO(deleted);
    }

    private Set<BeerPriceResponseDTO> mapToDto(Set<BeerPrice> beerPrices) {
        return beerPrices.stream()
                .map(BeerPriceResponseDTO::new)
                .collect(Collectors.toUnmodifiableSet());
    }
}
