package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{store_id}")
    public StoreResponseDTO get(@PathVariable("store_id") Long id) {
        return storeService.get(id);
    }

    @GetMapping(params = "city")
    public List<StoreResponseDTO> getAllInCity(@RequestParam("city") String city) {
        return storeService.getStores(city);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public List<StoreResponseDTO> getAll() {
        return storeService.getStores();
    }

    @PostMapping
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<StoreResponseDTO> add(@RequestBody @Valid StoreRequestDTO storeRequestDTO) {
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

    @PutMapping("/{store_id}")
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<StoreResponseDTO> update(@PathVariable("store_id") Long storeId,
                                                   @RequestBody @Valid StoreUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(storeService.update(storeId, updateDTO));
    }

    @DeleteMapping("/{store_id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public StoreDeleteDTO delete(@PathVariable("store_id") Long storeId) {
        return storeService.delete(storeId);
    }

}
