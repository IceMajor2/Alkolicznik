package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Beer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BeerRepository extends CrudRepository<Beer, Long> {

    Optional<Beer> findByName(String name);
}
