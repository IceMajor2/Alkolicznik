package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.services.BeerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class BeerController {

    private BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Fetch beer from database.
     * @param id beer id
     * @return Beer object wrapped in ResponseEntity class
     */
    @GetMapping("/api/beer/{id}")
    public ResponseEntity<Beer> getBeer(@PathVariable Long id) {
        Beer beer = beerService.get(id);
        if(beer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(beer);
    }

    /**
     * Add new beer to database.
     * @param beer Beer body
     * @return updated by database Beer object wrapped in ResponseEntity class
     */
    @PostMapping("/api/beer")
    public ResponseEntity<Beer> addBear(@RequestBody Beer beer) {
        Beer saved = beerService.save(beer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beer.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
}
