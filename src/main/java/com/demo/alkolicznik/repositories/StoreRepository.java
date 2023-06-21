package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoreRepository extends CrudRepository<Store, Long> {

    List<Store> findAll();

    boolean existsByName(String name);
}
