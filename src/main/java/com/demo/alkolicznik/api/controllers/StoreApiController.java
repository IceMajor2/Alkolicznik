package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.StoreRequestDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
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
@RequestMapping("/api/store")
public class StoreApiController {

    private StoreService storeService;

    public StoreApiController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStore(@PathVariable Long id) {
        Store store = storeService.get(id);
        if(store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(store);
    }

    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> getStores(@RequestParam String city) {
        List<Store> stores = storeService.getStores(city);
        List<StoreResponseDTO> storesDTO = stores.stream()
                .map(StoreResponseDTO::new)
                .toList();
        return ResponseEntity.ok(storesDTO);
    }

    @PostMapping
    public ResponseEntity<Store> addStore(@RequestBody @Valid StoreRequestDTO storeRequestDTO) {
        Store saved = storeService.add(storeRequestDTO);
        StoreResponseDTO responseDTO = new StoreResponseDTO(saved);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/{id}/beer")
    public ResponseEntity<BeerPriceResponseDTO> addBeer(
            @PathVariable Long id,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
        BeerPriceResponseDTO beerPriceResponse = storeService.addBeer(id, beerPriceRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPriceResponse.getBeer().getId())
                .toUri();
        return ResponseEntity.created(location).body(beerPriceResponse);
    }

    @GetMapping("/{id}/beer")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeers(@PathVariable("id") Long storeId) {
        Set<BeerPrice> prices = storeService.getBeers(storeId);
        List<BeerPriceResponseDTO> pricesDTO = prices.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(pricesDTO);
    }
}
