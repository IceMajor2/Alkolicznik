package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeerController {

    @GetMapping("/api/beer/{id}")
    public ResponseEntity<Beer> getBeer(@PathVariable Long id) {
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName("Perla");
        return ResponseEntity.ok(beer);
    }
}
