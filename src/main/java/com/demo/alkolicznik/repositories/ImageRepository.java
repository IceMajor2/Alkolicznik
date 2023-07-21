package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.ImageModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<ImageModel, Long> {

    Optional<ImageModel> findByImageUrl(String imageUrl);
}
