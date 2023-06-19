package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface StoreRepository extends CrudRepository<Store, Long> {

    /**
     * Find {@code BeerPrice} objects of a given store.
     * @param id store id
     * @return store's beers with their prices
     */
    Set<BeerPrice> findPricesById(Long id);
}
