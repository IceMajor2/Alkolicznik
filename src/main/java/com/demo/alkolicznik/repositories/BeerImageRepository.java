package com.demo.alkolicznik.repositories;

import java.util.List;
import java.util.Optional;
import com.demo.alkolicznik.models.image.BeerImage;
import org.springframework.data.repository.CrudRepository;

public interface BeerImageRepository extends CrudRepository<BeerImage, Long> {

    Optional<BeerImage> findByImageUrl(String imageUrl);

	List<BeerImage> findAll();
}
