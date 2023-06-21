package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.services.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
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
        return ResponseEntity.ok().body(beerPriceResponse);
    }
}
