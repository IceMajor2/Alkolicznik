package com.demo.alkolicznik.repositories;

import java.util.Optional;

import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.data.repository.CrudRepository;

public interface StoreImageRepository extends CrudRepository<StoreImage, Long> {

	Optional<StoreImage> findByImageUrl(String imageUrl);
}
