package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Store;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends CrudRepository<Store, Long> {

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices ORDER BY s.id ASC")
    List<Store> findAllByOrderByIdAsc();

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices")
    List<Store> findAll();

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices "
            + "WHERE s.city = :city ORDER BY s.id ASC")
    List<Store> findAllByCityOrderByIdAsc(@Param("city") String city);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.id = :id")
    Optional<Store> findById(@Param("id") Long id);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices "
            + "WHERE s.name = ?1 AND s.city = ?2 AND s.street = ?3")
    Optional<Store> findByNameAndCityAndStreet(String name, String city, String street);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE LOWER(s.name) = LOWER(?1)")
    List<Store> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT(s.name) FROM Store s")
    List<String> findDistinctNames();

    @Query("SELECT DISTINCT(s.city) FROM Store s")
    List<String> findDistinctCities();

    long countByName(String name);

    default Optional<Store> find(Store store) {
        return findByNameAndCityAndStreet(store.getName(),
                store.getCity(),
                store.getStreet());
    }

    boolean existsByCity(String city);

    boolean existsByNameAndCityAndStreet(String name, String city, String street);

    boolean existsByName(String name);

    default boolean exists(Store store) {
        return existsByNameAndCityAndStreet
                (store.getName(), store.getCity(), store.getStreet());
    }

    default boolean isNameUnique(String name) {
        return countByName(name) == 1;
    }
}
