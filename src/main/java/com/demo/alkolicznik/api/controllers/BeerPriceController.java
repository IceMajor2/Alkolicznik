package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.Set;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceUpdateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/beer-price")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public Set<BeerPriceResponseDTO> getAll() {
        return beerPriceService.getAll();
    }

    @GetMapping("/store/{store_id}/beer-price")
    public Set<BeerPriceResponseDTO> getAllByStoreId(@PathVariable("store_id") Long storeId) {
        return beerPriceService.getAllByStoreId(storeId);
    }

    @GetMapping(value = "/beer-price", params = "city")
    public List<BeerPriceResponseDTO> getAllByCity(@RequestParam("city") String city) {
        return beerPriceService.getAllByCity(city);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    public Set<BeerPriceResponseDTO> getAllByBeerId(@PathVariable("beer_id") Long beerId) {
        return beerPriceService.getAllByBeerId(beerId);
    }

    @GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
    public Set<BeerPriceResponseDTO> getAllByBeerIdAndCity(@PathVariable("beer_id") Long beerId,
                                                           @RequestParam("city") String city) {
        return beerPriceService.getAllByBeerIdAndCity(beerId, city);
    }

    @PostMapping("/store/{store_id}/beer-price")
    // secured in SecurityConfig
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerPriceResponseDTO> addByObject(
            @PathVariable("store_id") Long storeId,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
        BeerPriceResponseDTO beerPrice = beerPriceService.addByObject(storeId, beerPriceRequestDTO);
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
    public ResponseEntity<BeerPriceResponseDTO> addByParam(
            @PathVariable("store_id") Long storeId,
            @RequestParam("beer_id") Long beerId,
            @RequestParam("beer_price") @Positive(message = "Price must be a positive number") double price) {
        BeerPriceResponseDTO beerPrice = beerPriceService.addByParam(storeId, beerId, price);
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
    // secured in SecurityConfig
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerPriceResponseDTO> update(@RequestParam("store_id") Long storeId,
                                                       @RequestParam("beer_id") Long beerId,
                                                       @RequestBody @Valid BeerPriceUpdateDTO updateDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
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
