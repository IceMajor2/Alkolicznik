package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/beer/{beer_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<BeerResponseDTO> updateBeer(@PathVariable("beer_id") Long beerId,
                                                      @RequestBody @Valid BeerUpdateDTO updateDTO) {
        Beer updated = beerService.update(beerId, updateDTO);
        BeerResponseDTO updatedResponse = new BeerResponseDTO(updated);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedResponse);
    }

    @DeleteMapping("/beer/{beer_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<BeerDeleteDTO> updateStore(@PathVariable("beer_id") Long beerId) {
        Beer deleted = beerService.delete(beerId);
        BeerDeleteDTO deletedResponse = new BeerDeleteDTO(deleted);
        return ResponseEntity.ok(deletedResponse);
    }

    @PutMapping("/store/{store_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<StoreResponseDTO> updateStore(@PathVariable("store_id") Long storeId,
                                                        @RequestBody @Valid StoreUpdateDTO updateDTO) {
        Store updated = storeService.update(storeId, updateDTO);
        StoreResponseDTO updatedResponse = new StoreResponseDTO(updated);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedResponse);
    }

    @DeleteMapping("/store/{store_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<StoreDeleteDTO> deleteStore(@PathVariable("store_id") Long storeId) {
        Store deleted = storeService.delete(storeId);
        StoreDeleteDTO deleteResponseDTO = new StoreDeleteDTO(deleted);
        return ResponseEntity.ok(deleteResponseDTO);
    }

    @PutMapping("/beer-price")
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "Basic Authentication") // OpenAPI
    public ResponseEntity<BeerPriceResponseDTO> updateBeerPrice(@RequestParam("store_id") Long storeId,
                                                                @RequestParam("beer_id") Long beerId,
                                                                @RequestBody @Valid BeerPriceUpdateDTO updateDTO) {
        BeerPrice updated = beerPriceService.update(storeId, beerId, updateDTO);
        BeerPriceResponseDTO response = new BeerPriceResponseDTO(updated);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
