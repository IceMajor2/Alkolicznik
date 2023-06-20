package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.services.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

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
        System.out.println(store);
        Store saved = storeService.add(store);
        System.out.println(saved);
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
}
