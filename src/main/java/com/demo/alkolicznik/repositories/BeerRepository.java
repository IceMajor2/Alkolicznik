package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Beer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BeerRepository extends CrudRepository<Beer, Long> {

    Optional<Beer> findByBrand(String brand);

    @Query(value =
            "SELECT b FROM Beer b WHERE concat(brand, ' ', type) = ?1")
    Optional<Beer> findByFullname(String fullname);

    List<Beer> findAll();
}
