package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.requests.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/store/{store_id}/beer-price")
    public ResponseEntity<BeerPriceResponseDTO> addBeerPrice(
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
    public ResponseEntity<BeerPriceResponseDTO> addBeerPrice(
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

    @GetMapping("/store/{store_id}/beer-price")
    public Set<BeerPriceResponseDTO> getBeerPricesStoreId(@PathVariable("store_id") Long storeId) {
        return beerPriceService.getBeerPricesOnStoreId(storeId);
    }

    @GetMapping(value = "/beer-price", params = "city")
    public Set<BeerPriceResponseDTO> getBeerPrices(@RequestParam("city") String city) {
        return beerPriceService.getBeerPrices(city);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    public Set<BeerPriceResponseDTO> getBeersPrice(@PathVariable("beer_id") Long beerId) {
        return beerPriceService.getBeerPricesOnBeerId(beerId);
    }

    @GetMapping(value = "/beer-price", params = {"store_id", "beer_id"})
    public BeerPriceResponseDTO getBeerPrice(@RequestParam("store_id") Long storeId,
                                             @RequestParam("beer_id") Long beerId) {
        return beerPriceService.get(storeId, beerId);
    }

    @GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
    public Set<BeerPriceResponseDTO> getBeersPrice(@PathVariable("beer_id") Long beerId,
                                                   @RequestParam("city") String city) {
        return beerPriceService.getBeerPrices(beerId, city);
    }
}
