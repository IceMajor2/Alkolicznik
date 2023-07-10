package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.delete.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private BeerService beerService;
    private StoreService storeService;
    private BeerPriceService beerPriceService;

    public AdminController(BeerService beerService, StoreService storeService, BeerPriceService beerPriceService) {
        this.beerService = beerService;
        this.storeService = storeService;
        this.beerPriceService = beerPriceService;
    }

    @GetMapping("/store")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public List<StoreResponseDTO> getAllStores() {
        return storeService.getStores();
    }

    @GetMapping("/beer")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public List<BeerResponseDTO> getAllBeers() {
        return beerService.getBeers();
    }

    @GetMapping("/beer-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public Set<BeerPriceResponseDTO> getAllBeerPrices() {
        return beerPriceService.getBeerPrices();
    }

    @PutMapping("/beer/{beer_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<BeerResponseDTO> updateBeer(@PathVariable("beer_id") Long beerId,
                                                      @RequestBody @Valid BeerUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(beerService.update(beerId, updateDTO));
    }

    @DeleteMapping("/beer/{beer_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public BeerDeleteDTO deleteBeer(@PathVariable("beer_id") Long beerId) {
        return beerService.delete(beerId);
    }

    @PutMapping("/store/{store_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<StoreResponseDTO> updateStore(@PathVariable("store_id") Long storeId,
                                                        @RequestBody @Valid StoreUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(storeService.update(storeId, updateDTO));
    }

    @DeleteMapping("/store/{store_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public StoreDeleteDTO deleteStore(@PathVariable("store_id") Long storeId) {
        return storeService.delete(storeId);
    }

    @PutMapping("/beer-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<BeerPriceResponseDTO> updateBeerPrice(@RequestParam("store_id") Long storeId,
                                                                @RequestParam("beer_id") Long beerId,
                                                                @RequestBody @Valid BeerPriceUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(beerPriceService.update(storeId, beerId, updateDTO));
    }

    @DeleteMapping("/beer-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public BeerPriceDeleteDTO deleteBeerPrice(@RequestParam("store_id") Long storeId,
                                              @RequestParam("beer_id") Long beerId) {
        return beerPriceService.delete(storeId, beerId);
    }
}
