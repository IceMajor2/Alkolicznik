package com.demo.alkolicznik.controllers;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.services.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/store/{id}/beers")
    public ResponseEntity<Set<Beer>> getBeersInStore(@PathVariable Long id) {
        return null;
    }
}
