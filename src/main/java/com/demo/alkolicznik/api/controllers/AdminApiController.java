package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private BeerService beerService;
    private StoreService storeService;
    private BeerPriceService beerPriceService;

    public AdminApiController(BeerService beerService, StoreService storeService, BeerPriceService beerPriceService) {
        this.beerService = beerService;
        this.storeService = storeService;
        this.beerPriceService = beerPriceService;
    }

    @GetMapping("/store")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getStores());
    }

    @GetMapping("/beer")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<List<BeerResponseDTO>> getAllBeers() {
        List<Beer> beers = beerService.getBeers();
        List<BeerResponseDTO> beersDTO = beers.stream()
                .map(BeerResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beersDTO);
    }

    @GetMapping("/beer-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<List<BeerPriceResponseDTO>> getAllBeerPrices() {
        Set<BeerPrice> prices = beerPriceService.getBeerPrices();
        List<BeerPriceResponseDTO> pricesDTO = prices.stream()
                .map(BeerPriceResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pricesDTO);
    }
}
