package com.demo.alkolicznik.api.services;

import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.classes.EntitiesNotFoundException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.PriceIsSameException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.money.Monetary;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerPriceService {

    private final StoreRepository storeRepository;
    private final BeerRepository beerRepository;

    @Transactional(readOnly = true)
    public List<BeerPriceResponseDTO> getAllByStoreId(Long storeId) {
        throwExceptionIfStoreNotFound(storeId);
        Store store = storeRepository.findById(storeId).get();
        Set<BeerPrice> prices = new TreeSet<>(comparatorByBeerIdAndPrice());
        prices.addAll(store.getPrices());
        return BeerPriceResponseDTO.asList(prices);
    }

    @Transactional(readOnly = true)
    public BeerPriceResponseDTO get(Long storeId, Long beerId) {
        throwIfNotFoundAll(storeId, beerId);
        Store store = storeRepository.findById(storeId).get();
        return new BeerPriceResponseDTO(store.findBeer(beerId).orElseThrow(
                () -> new BeerPriceNotFoundException()
        ));
    }

    @Transactional(readOnly = true)
    public List<BeerPriceResponseDTO> getAll() {
        List<Store> stores = storeRepository.findAll();
        Set<BeerPrice> prices = new TreeSet<>(comparatorByCityBeerIdPriceAndStoreId());
        stores.forEach(store -> prices.addAll(store.getPrices()));
        return BeerPriceResponseDTO.asList(prices);
    }

    @Transactional(readOnly = true)
    public List<BeerPriceResponseDTO> getAllByCity(String city) {
        List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);

        if (cityStores.isEmpty()) {
            throw new NoSuchCityException(city);
        }
        Set<BeerPrice> prices = new TreeSet<>(comparatorByBeerIdPriceAndStoreId());
        cityStores.forEach(store -> prices.addAll(store.getPrices()));
        return BeerPriceResponseDTO.asList(prices);
    }

    @Transactional(readOnly = true)
    public List<BeerPriceResponseDTO> getAllByBeerId(Long beerId) {
        throwExceptionIfBeerNotFound(beerId);
        List<Store> stores = storeRepository.findAll();

        Set<BeerPrice> prices = new TreeSet<>(comparatorByCityPriceAndStoreId());
        stores.forEach(store -> store.findBeer(beerId).ifPresent(price -> prices.add(price)));
        return BeerPriceResponseDTO.asList(prices);
    }

    @Transactional(readOnly = true)
    public List<BeerPriceResponseDTO> getAllByBeerIdAndCity(Long beerId, String city) {
        if (!beerRepository.existsById(beerId) && !storeRepository.existsByCity(city)) {
            throw new EntitiesNotFoundException(beerId, city);
        }
        throwExceptionIfBeerNotFound(beerId);
        Beer beer = beerRepository.findById(beerId).get();
        if (!storeRepository.existsByCity(city)) {
            throw new NoSuchCityException(city);
        }
        Set<BeerPrice> beerPricesInCity = new TreeSet<>(comparatorByPriceAndStoreId());
        beer.getPrices().stream()
                .filter(price -> price.getStore().getCity().equals(city))
                .forEach(price -> beerPricesInCity.add(price));
        return BeerPriceResponseDTO.asList(beerPricesInCity);
    }

    @Transactional(readOnly = false)
    public BeerPriceResponseDTO addByObject(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
        String beerFullname = beerPriceRequestDTO.getBeerName();
        double volume = beerPriceRequestDTO.getBeerVolume();
        // CONDITIONS: start
        if (!storeRepository.existsById(storeId) && !beerRepository
                .existsByFullnameAndVolume(beerFullname, volume)) {
            throw new EntitiesNotFoundException(beerFullname, volume, storeId);
        }
        throwExceptionIfStoreNotFound(storeId);
        Store store = storeRepository.findById(storeId).get();
        Beer beer = beerRepository.findByFullnameAndVolume(beerFullname, volume).orElseThrow(
                () -> new BeerNotFoundException(beerFullname, volume)
        );
        if (store.findBeer(beer.getId()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }
        // CONDITIONS: end
        double price = beerPriceRequestDTO.getPrice();
        store.saveBeer(beer, price);
        storeRepository.save(store);
        BeerPriceResponseDTO saved = new BeerPriceResponseDTO(store.findBeer(beer.getId()).get());
        log.info("Added: [{}]", saved);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, noRollbackFor = Exception.class)
    public BeerPriceResponseDTO addByParam(Long storeId, Long beerId, Double price) {
        throwIfNotFoundAll(storeId, beerId);
        Store store = storeRepository.findById(storeId).get();
        Beer beer = beerRepository.findById(beerId).get();

        if (store.findBeer(beer.getId()).isPresent()) {
            throw new BeerPriceAlreadyExistsException();
        }

        store.saveBeer(beer, price);
        storeRepository.save(store);
        BeerPriceResponseDTO saved = new BeerPriceResponseDTO(store.findBeer(beerId).get());
        log.info("Added: [{}]", saved);
        return saved;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public BeerPriceResponseDTO update(Long storeId, Long beerId, Double price) {
        throwIfNotFoundAll(storeId, beerId);
        Store store = storeRepository.findById(storeId).get();
        BeerPrice toUpdate = store.findBeer(beerId).orElseThrow(() ->
                new BeerPriceNotFoundException());
        BeerPrice updated = toUpdatedModel(toUpdate, price);
        if (Objects.equals(toUpdate.getAmountOnly(), updated.getAmountOnly())) {
            throw new PriceIsSameException(toUpdate.getPrice().toString());
        }
        String previousPrice = toUpdate.getPrice().toString();
        toUpdate.setPrice(updated.getPrice());
        storeRepository.save(store);
        BeerPriceResponseDTO saved = new BeerPriceResponseDTO(updated);
        log.info("Updating price of [{}] in [{}] from: [{}] to: [{}]",
                saved.getBeer().getFullName(), saved.getStore().prettyString(),
                previousPrice, updated.getPrice());
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, noRollbackFor = Exception.class)
    public BeerPriceDeleteDTO delete(Long storeId, Long beerId) {
        throwIfNotFoundAll(storeId, beerId);
        Store store = storeRepository.findById(storeId).get();
        Beer beer = beerRepository.findById(beerId).get();

        store.findBeer(beerId).orElseThrow(() -> new BeerPriceNotFoundException());

        BeerPrice deleted = store.deleteBeer(beer);
        beerRepository.save(beer);
        storeRepository.save(store);
        BeerPriceDeleteDTO deletedDTO = new BeerPriceDeleteDTO(deleted);
        log.info("Deleted [{} of {}] from [{}]",
                deletedDTO.getBeer().getFullName(), deletedDTO.getPrice(), deletedDTO.getStore().prettyString());
        return deletedDTO;
    }

    private Comparator comparatorByBeerIdPriceAndStoreId() {
        return Comparator.comparing(p -> ((BeerPrice) p).getBeer().getId())
                .thenComparing(p -> ((BeerPrice) p).getAmountOnly())
                .thenComparing(p -> ((BeerPrice) p).getStore().getId());
    }

    private Comparator comparatorByCityPriceAndStoreId() {
        return Comparator.comparing(p -> ((BeerPrice) p).getStore().getCity())
                .thenComparing(p -> ((BeerPrice) p).getAmountOnly())
                .thenComparing(p -> ((BeerPrice) p).getStore().getId());
    }

    private Comparator comparatorByPriceAndStoreId() {
        return Comparator.comparing(p -> ((BeerPrice) p).getAmountOnly())
                .thenComparing(p -> ((BeerPrice) p).getStore().getId());
    }

    private Comparator comparatorByBeerIdAndPrice() {
        return Comparator.comparing(p -> ((BeerPrice) p).getBeer().getId())
                .thenComparing(p -> ((BeerPrice) p).getAmountOnly());
    }

    private Comparator comparatorByCityBeerIdPriceAndStoreId() {
        return Comparator.comparing(p -> ((BeerPrice) p).getStore().getCity())
                .thenComparing(p -> ((BeerPrice) p).getBeer().getId())
                .thenComparing(p -> ((BeerPrice) p).getAmountOnly())
                .thenComparing(p -> ((BeerPrice) p).getStore().getId());
    }

    private void throwIfNotFoundAll(Long storeId, Long beerId) {
        throwExceptionIfBothStoreAndBeerNotFound(storeId, beerId);
        throwExceptionIfBeerNotFound(beerId);
        throwExceptionIfStoreNotFound(storeId);
    }

    private void throwExceptionIfBeerNotFound(Long beerId) {
        if (!beerRepository.existsById(beerId)) {
            throw new BeerNotFoundException(beerId);
        }
    }

    private void throwExceptionIfStoreNotFound(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }
    }

    private void throwExceptionIfBothStoreAndBeerNotFound(Long storeId, Long beerId) {
        if (!storeRepository.existsById(storeId) && !beerRepository.existsById(beerId)) {
            throw new EntitiesNotFoundException(beerId, storeId);
        }
    }

    private BeerPrice toUpdatedModel(BeerPrice toUpdate, Double withPrice) {
        BeerPrice updated = toUpdate.clone();
        updated.setPrice(Monetary.getDefaultAmountFactory()
                .setCurrency("PLN")
                .setNumber(withPrice)
                .create());
        return updated;
    }
}
