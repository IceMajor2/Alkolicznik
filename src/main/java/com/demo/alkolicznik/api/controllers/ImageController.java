package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ImageController {

    private StoreImageService storeImageService;

    private BeerImageService beerImageService;

    @GetMapping("/beer/{beer_id}/image")
    public BeerImageResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
        return beerImageService.get(beerId);
    }

    @GetMapping("/beer/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<BeerImageResponseDTO> getAllBeerImages() {
        return beerImageService.getAll();
    }

    @PostMapping("/beer/{beer_id}/image")
    public ResponseEntity<BeerImageResponseDTO> addBeerImage(
            @PathVariable("beer_id") Long beerId,
            @RequestBody @Valid ImageRequestDTO request) {
        BeerImageResponseDTO response = beerImageService.add(beerId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerId)
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @DeleteMapping("/beer/{beer_id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity deleteBeerImage(@PathVariable("beer_id") Long beerId) {
        beerImageService.delete(beerId);
        return ResponseEntity.ok(Map.of("message", "Beer image was deleted successfully!"));
    }

    @GetMapping("/store/{store_id}/image")
    public StoreImageResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
        return storeImageService.get(storeId);
    }

    @GetMapping("/store/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public List<StoreImageResponseDTO> getAllStoreImages() {
        return storeImageService.getAll();
    }

    @GetMapping(value = "/store/image", params = "name")
    public StoreImageResponseDTO getStoreImage(@RequestParam("name") String storeName) {
        storeName = storeName.replace("%20", " ");
        return storeImageService.get(storeName);
    }

    @PostMapping("/store/image")
    public ResponseEntity<StoreImageResponseDTO> addStoreImage(
            @RequestParam("name") String storeName,
            @RequestBody @Valid ImageRequestDTO imageRequestDTO) {
        // space in a path is represented by '%20' string, thus
        // we need to replace it with actual space char to get a valid name
        storeName = storeName.replace("%20", " ");
        StoreImageResponseDTO response = storeImageService.add(storeName, imageRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("name", storeName)
                .build()
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/store/image")
    public StoreImageResponseDTO updateStoreImage(@RequestParam("name") String storeName,
                                                  @RequestBody @Valid ImageRequestDTO imageRequestDTO) {
        storeName = storeName.replace("%20", " ");
        return storeImageService.update(storeName, imageRequestDTO);
    }

    @DeleteMapping("/store/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity deleteStoreImage(@RequestParam("name") String storeName) {
        storeName = storeName.replace("%20", " ");
        storeImageService.delete(storeName);
        return ResponseEntity.ok(Map.of("message", "Store image was deleted successfully!"));
    }
}
