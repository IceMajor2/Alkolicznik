package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.services.BeerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BeerController {

    private BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Fetch beer from database.
     * @param id beer id passed in path
     * @return Beer object wrapped in ResponseEntity class
     */
    @GetMapping("/beer/{id}")
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
    @PostMapping("/beer")
    public ResponseEntity<Beer> addBeer(@RequestBody Beer beer) {
        Beer saved = beerService.add(beer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beer.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/beer")
    public ResponseEntity<List<Beer>> getBeers() {
        List<Beer> beers = beerService.getBeers();
        return ResponseEntity.ok(beers);
    }
}
