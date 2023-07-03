package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Beer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BeerRepository extends CrudRepository<Beer, Long> {

    Optional<Beer> findByBrand(String brand);

    @Query(value = "SELECT * FROM beer b WHERE concat_ws(' ', brand, type) = ?1 AND volume = ?2", nativeQuery = true)
    Optional<Beer> findByFullnameAndVolume(String fullname, Double volume);

    List<Beer> findAll();

    default boolean exists(Beer beer) {
        String brand = beer.getBrand();
        String type = beer.getType();
        Double volume = beer.getVolume();
        return existsByBrandAndTypeAndVolume(brand, type, volume);
    }

    boolean existsByBrandAndTypeAndVolume(String brand, String type, Double volume);
}
