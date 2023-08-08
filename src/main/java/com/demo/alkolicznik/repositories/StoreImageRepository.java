package com.demo.alkolicznik.repositories;

import java.util.List;
import java.util.Optional;

import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.data.repository.CrudRepository;

public interface StoreImageRepository extends CrudRepository<StoreImage, Long> {

	List<StoreImage> findAll();

	Optional<StoreImage> findByImageUrl(String imageUrl);

	Optional<StoreImage> findByStoreName(String storeName);
}
