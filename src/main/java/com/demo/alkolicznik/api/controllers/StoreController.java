package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{store_id}")
    public StoreResponseDTO getStore(@PathVariable("store_id") Long id) {
        return storeService.get(id);
    }

    @GetMapping
    public List<StoreResponseDTO> getStores(@RequestParam String city) {
        return storeService.getStores(city);
    }

    @PostMapping
    public ResponseEntity<StoreResponseDTO> addStore(@RequestBody @Valid StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO saved = storeService.add(storeRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(saved);
    }
}
