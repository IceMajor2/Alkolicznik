package com.demo.alkolicznik.repositories;

import java.util.List;
import java.util.Optional;

import com.demo.alkolicznik.models.image.StoreImage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StoreImageRepository extends CrudRepository<StoreImage, Long> {

	List<StoreImage> findAll();

	Optional<StoreImage> findByImageUrl(String imageUrl);

	Optional<StoreImage> findByStoreName(String storeName);

	@Query(value = "SELECT id FROM store_image WHERE store_name = ?1", nativeQuery = true)
	Optional<Long> findIdByStoreName(@Param("store_name") String storeName);
}
