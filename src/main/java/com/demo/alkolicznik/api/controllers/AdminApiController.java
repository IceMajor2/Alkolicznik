package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private BeerService beerService;
    private StoreService storeService;

    public AdminApiController(BeerService beerService, StoreService storeService) {
        this.beerService = beerService;
        this.storeService = storeService;
    }

    @GetMapping("/store")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getStores());
    }

    @GetMapping("/beer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Beer>> getAllBeers() {
        return ResponseEntity.ok(beerService.getBeers());
    }
}
