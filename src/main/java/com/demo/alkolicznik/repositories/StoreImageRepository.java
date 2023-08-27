package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.image.StoreImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StoreImageRepository extends CrudRepository<StoreImage, Long> {

    @Query("SELECT i FROM StoreImage i LEFT JOIN FETCH i.stores")
    List<StoreImage> findAll();

    Optional<StoreImage> findByImageUrl(String imageUrl);

    Optional<StoreImage> findByStoreName(String storeName);

    boolean existsByStoreName(String storeName);
}
