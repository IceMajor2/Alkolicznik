package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getStores());
    }

    @GetMapping("/beer")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<BeerResponseDTO>> getAllBeers() {
        List<Beer> beers = beerService.getBeers();
        List<BeerResponseDTO> beersDto = beers.stream()
                .map(beerService::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beersDto);
    }
}