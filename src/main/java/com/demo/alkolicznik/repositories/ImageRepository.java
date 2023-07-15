package com.demo.alkolicznik.repositories;

import com.demo.alkolicznik.models.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long> {
}
