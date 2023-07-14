package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Beer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BeerRepository extends CrudRepository<Beer, Long> {

    @Query("SELECT b FROM Beer b LEFT JOIN FETCH b.prices WHERE b.id = :id")
    Optional<Beer> findById(@Param("id") Long id);

    @Query(value = "SELECT * FROM beer b WHERE concat_ws(' ', brand, type) = ?1 AND volume = ?2", nativeQuery = true)
    Optional<Beer> findByFullnameAndVolume(String fullname, Double volume);

//    @Query(value = "SELECT b.id FROM beer b WHERE concat_ws(' ', brand, type) = ?1 AND volume = ?2", nativeQuery = true)
//    Long findIdByFullnameAndVolume(String fullname, Double volume);

    @Query(value = "SELECT count(*) > 0 FROM beer b WHERE concat_ws(' ', brand, type) = ?1", nativeQuery = true)
    boolean existsByFullname(String fullname);

    List<Beer> findAllByOrderByIdAsc();

    default boolean exists(Beer beer) {
        String brand = beer.getBrand();
        String type = beer.getType();
        Double volume = beer.getVolume();
        return existsByBrandAndTypeAndVolume(brand, type, volume);
    }

    boolean existsByBrandAndTypeAndVolume(String brand, String type, Double volume);
}
