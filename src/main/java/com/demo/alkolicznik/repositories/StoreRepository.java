package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends CrudRepository<Store, Long> {

    List<Store> findAll();

    //@Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.city = :city")
    List<Store> findAllByCity(@Param("city") String city);

    boolean existsByCity(String city);

    boolean existsByNameAndCityAndStreet(String name, String city, String street);
}
