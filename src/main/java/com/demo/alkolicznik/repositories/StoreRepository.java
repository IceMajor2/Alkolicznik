package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
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

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.city = :city")
    List<Store> findAllByCity(@Param("city") String city);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.city = :city ORDER BY s.id ASC")
    List<Store> findAllByCityOrderByIdAsc(@Param("city") String city);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.id = :id")
    Optional<Store> findById(@Param("id") Long id);

    @Query("SELECT s FROM Store s LEFT JOIN FETCH s.prices WHERE s.name = ?1 AND s.city = ?2 AND s.street = ?3")
    Optional<Store> findByNameAndCityAndStreet(String name, String city, String street);

    default Optional<Store> findByStoreRequest(StoreRequestDTO storeRequestDTO) {
        return findByNameAndCityAndStreet(storeRequestDTO.getName(),
                storeRequestDTO.getCity(),
                storeRequestDTO.getStreet());
    }

    boolean existsByCity(String city);

    boolean existsByNameAndCityAndStreet(String name, String city, String street);
}
