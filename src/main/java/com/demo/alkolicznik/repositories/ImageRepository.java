package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.ImageModel;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<ImageModel, Long> {
}
