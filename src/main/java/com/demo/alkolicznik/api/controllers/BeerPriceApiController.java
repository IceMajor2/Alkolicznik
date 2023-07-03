package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.api.services.StoreService;
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

    private StoreService storeService;
    private BeerService beerService;

    public BeerPriceApiController(StoreService storeService, BeerService beerService) {
        this.storeService = storeService;
        this.beerService = beerService;
    }

    @PostMapping("/store/{store_id}/beer-price")
    public ResponseEntity<BeerPriceResponseDTO> addBeerPrice(
            @PathVariable("store_id") Long storeId,
            @RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {

        BeerPrice beerPrice = storeService.addBeer(storeId, beerPriceRequestDTO);
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

        BeerPrice beerPrice = storeService.addBeer(storeId, beerId, price);
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
        Set<BeerPrice> prices = storeService.getBeers(storeId);
        List<BeerPriceResponseDTO> pricesDTO = prices.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(pricesDTO);
    }

    @GetMapping("/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPrices(@RequestParam("city") String city) {
        List<BeerPrice> beers = beerService.getBeerPrices(city);
        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersResponse);
    }

    @GetMapping("/beer/{beer_id}/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPricesBeerId(@PathVariable("beer_id") Long beerId) {
        List<BeerPrice> beers = beerService.getBeerPrices(beerId);
        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersResponse);
    }
}
