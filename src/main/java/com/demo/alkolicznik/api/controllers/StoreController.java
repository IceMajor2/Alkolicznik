package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.requests.StoreRequestDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping(params = "city")
    public List<StoreResponseDTO> getStoresInCity(@RequestParam("city") String city) {
        return storeService.getStores(city);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public List<StoreResponseDTO> getStores() {
        return storeService.getStores();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
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
