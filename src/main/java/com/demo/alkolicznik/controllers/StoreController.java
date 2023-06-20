package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.services.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/store/{id}/beers")
    public ResponseEntity<Set<BeerPrice>> getBeersInStore(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/store")
    public ResponseEntity<List<Store>> getStores() {
        return ResponseEntity.ok(storeService.getStores());
    }
}
