package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.api.services.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StoreApiController {

    private StoreService storeService;

    public StoreApiController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/store/{id}")
    public ResponseEntity<Store> getStore(@PathVariable Long id) {
        Store store = storeService.get(id);
        if(store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }

    @GetMapping("/store")
    public ResponseEntity<List<Store>> getStores() {
        return ResponseEntity.ok(storeService.getStores());
    }

    @PostMapping("/store")
    public ResponseEntity<Store> addStore(@RequestBody @Valid Store store) {
        Store saved = storeService.add(store);
        if(saved == null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(store.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/store/{id}/beer")
    public ResponseEntity<BeerPriceResponseDTO> addBeer(
            @PathVariable Long id,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
        BeerPriceResponseDTO beerPriceResponse = storeService.addBeer(id, beerPriceRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPriceResponse.getBeerId())
                .toUri();
        System.out.println(location);
        return ResponseEntity.created(location).body(beerPriceResponse);
    }

    @GetMapping("/store/{id}/beer")
    public ResponseEntity<Set<BeerPrice>> getBeers(@PathVariable("id") Long storeId) {
        Set<BeerPrice> beers = storeService.getBeers(storeId);
        return ResponseEntity.ok(beers);
    }
}
