package com.demo.alkolicznik.repositories;

import java.util.Optional;
import com.demo.alkolicznik.models.image.BeerImage;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<BeerImage, Long> {

    Optional<BeerImage> findByImageUrl(String imageUrl);
}
