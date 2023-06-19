package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Beer;
import org.springframework.data.repository.CrudRepository;

public interface BeerRepository extends CrudRepository<Beer, Long> {
}
