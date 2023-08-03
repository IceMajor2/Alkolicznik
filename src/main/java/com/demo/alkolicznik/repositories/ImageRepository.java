package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.BeerImage;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<BeerImage, Long> {

    Optional<BeerImage> findByImageUrl(String imageUrl);
}
