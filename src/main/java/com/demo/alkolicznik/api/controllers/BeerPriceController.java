package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.delete.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api")
@Validated
public class BeerPriceController {

    private BeerPriceService beerPriceService;

    public BeerPriceController(BeerPriceService beerPriceService) {
        this.beerPriceService = beerPriceService;
    }

    @GetMapping(value = "/beer-price", params = {"store_id", "beer_id"})
    public BeerPriceResponseDTO get(@RequestParam("store_id") Long storeId,
                                    @RequestParam("beer_id") Long beerId) {
        return beerPriceService.get(storeId, beerId);
    }

    @GetMapping("/store/{store_id}/beer-price")
    public Set<BeerPriceResponseDTO> getAllByStoreId(@PathVariable("store_id") Long storeId) {
        return beerPriceService.getBeerPricesOnStoreId(storeId);
    }

    @GetMapping(value = "/beer-price", params = "city")
    public Set<BeerPriceResponseDTO> getAllByCity(@RequestParam("city") String city) {
        return beerPriceService.getBeerPrices(city);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    public Set<BeerPriceResponseDTO> getAllByBeerId(@PathVariable("beer_id") Long beerId) {
        return beerPriceService.getBeerPricesOnBeerId(beerId);
    }

    @GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
    public Set<BeerPriceResponseDTO> getAllByBeerAndCity(@PathVariable("beer_id") Long beerId,
                                            @RequestParam("city") String city) {
        return beerPriceService.getBeerPrices(beerId, city);
    }

    @GetMapping("/beer-price")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public Set<BeerPriceResponseDTO> getAll() {
        return beerPriceService.getBeerPrices();
    }

    @PostMapping("/store/{store_id}/beer-price")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerPriceResponseDTO> add(
            @PathVariable("store_id") Long storeId,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
        BeerPriceResponseDTO beerPrice = beerPriceService.add(storeId, beerPriceRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPrice.getBeer().getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(beerPrice);
    }

    @PostMapping(value = "/store/{store_id}/beer-price", params = {"beer_id", "beer_price"})
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerPriceResponseDTO> add(
            @PathVariable("store_id") Long storeId,
            @RequestParam("beer_id") Long beerId,
            @RequestParam("beer_price") @Positive(message = "Price must be a positive number") double price) {
        BeerPriceResponseDTO beerPrice = beerPriceService.add(storeId, beerId, price);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPrice.getBeer().getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(beerPrice);
    }

    @PutMapping("/beer-price")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerPriceResponseDTO> update(@RequestParam("store_id") Long storeId,
                                                       @RequestParam("beer_id") Long beerId,
                                                       @RequestBody @Valid BeerPriceUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(beerPriceService.update(storeId, beerId, updateDTO));
    }

    @DeleteMapping("/beer-price")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public BeerPriceDeleteDTO delete(@RequestParam("store_id") Long storeId,
                                     @RequestParam("beer_id") Long beerId) {
        return beerPriceService.delete(storeId, beerId);
    }
}
