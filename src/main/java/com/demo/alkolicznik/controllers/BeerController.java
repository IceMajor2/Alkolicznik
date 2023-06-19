package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.repositories.BeerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class BeerController {

    private BeerRepository beerRepository;

    public BeerController(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @GetMapping("/api/beer/{id}")
    public ResponseEntity<Beer> getBeer(@PathVariable Long id) {
        Beer beer = beerRepository.findById(id).get();
        return ResponseEntity.ok(beer);
    }

    @PostMapping("/api/beer")
    public ResponseEntity<Beer> addBear(@RequestBody Beer beer) {
        Beer saved = beerRepository.save(beer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beer.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
}
