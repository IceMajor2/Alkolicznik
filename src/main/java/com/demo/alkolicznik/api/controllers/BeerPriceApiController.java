package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.BeerPrice;
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
@RequestMapping("/api")
@Validated
public class BeerPriceApiController {

    private BeerPriceService beerPriceService;

    public BeerPriceApiController(BeerPriceService beerPriceService) {
        this.beerPriceService = beerPriceService;
    }

    @PostMapping("/store/{store_id}/beer-price")
    public ResponseEntity<BeerPriceResponseDTO> addBeerPrice(
            @PathVariable("store_id") Long storeId,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {

        BeerPrice beerPrice = beerPriceService.add(storeId, beerPriceRequestDTO);
        BeerPriceResponseDTO beerPriceResponse = new BeerPriceResponseDTO(beerPrice);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPriceResponse.getBeer().getId())
                .toUri();
        return ResponseEntity.created(location).body(beerPriceResponse);
    }

    @PostMapping(value = "/store/{store_id}/beer-price", params = {"beer_id", "beer_price"})
    public ResponseEntity<BeerPriceResponseDTO> addBeerPrice(
            @PathVariable("store_id") Long storeId,
            @RequestParam("beer_id") Long beerId,
            @RequestParam("beer_price") @Positive(message = "Price must be a positive number") double price) {

        BeerPrice beerPrice = beerPriceService.add(storeId, beerId, price);
        BeerPriceResponseDTO beerPriceResponse = new BeerPriceResponseDTO(beerPrice);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beerPriceResponse.getBeer().getId())
                .toUri();
        return ResponseEntity.created(location).body(beerPriceResponse);
    }

    @GetMapping("/store/{store_id}/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPricesStoreId(@PathVariable("store_id") Long storeId) {
        Set<BeerPrice> prices = beerPriceService.getBeerPricesOnStoreId(storeId);
        List<BeerPriceResponseDTO> pricesDTO = prices.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(pricesDTO);
    }

    @GetMapping(value = "/beer-price", params = "city")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPrices(@RequestParam("city") String city) {
        List<BeerPrice> beers = beerPriceService.getBeerPrices(city);
        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersResponse);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPricesBeerId(@PathVariable("beer_id") Long beerId) {
        List<BeerPrice> beers = beerPriceService.getBeerPricesOnBeerId(beerId);
        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersResponse);
    }

    @GetMapping(value = "/beer-price", params = {"store_id", "beer_id"})
    public ResponseEntity<BeerPriceResponseDTO> getBeerPrice(@RequestParam("store_id") Long storeId,
                                                             @RequestParam("beer_id") Long beerId) {
        BeerPrice beerPrice = beerPriceService.get(storeId, beerId);
        BeerPriceResponseDTO beerPriceResponse = new BeerPriceResponseDTO(beerPrice);
        return ResponseEntity.ok(beerPriceResponse);
    }

    @GetMapping("/{beer_id}/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPrice(@PathVariable("beer_id") Long beerId,
                                                                   @RequestParam("city") String city) {
        return null;
    }
}
