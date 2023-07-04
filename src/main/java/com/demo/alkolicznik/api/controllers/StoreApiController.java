package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.dto.*;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.api.services.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/{store_id}")
    public ResponseEntity<StoreResponseDTO> getStore(@PathVariable("store_id") Long id) {
        Store store = storeService.get(id);
        StoreResponseDTO storeResponse = new StoreResponseDTO(store);
        if (store == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(storeResponse);
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
}
