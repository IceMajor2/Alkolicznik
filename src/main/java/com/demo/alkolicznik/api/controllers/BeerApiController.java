package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.models.BeerPrice;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beer")
public class BeerApiController {

    private BeerService beerService;

    public BeerApiController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponseDTO> getBeer(@PathVariable Long id) {
        Beer beer = beerService.get(id);
        BeerResponseDTO beerDto = new BeerResponseDTO(beer);
        return ResponseEntity.ok(beerDto);
    }

    @GetMapping
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeers(@RequestParam("city") String city) {
        List<BeerPrice> beers = beerService.getBeers(city);

        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beersResponse);
    }

    @PostMapping
    public ResponseEntity<BeerResponseDTO> addBeer(@RequestBody @Valid BeerRequestDTO beerRequestDTO) {
        try {
            Beer saved = beerService.add(beerRequestDTO);
            BeerResponseDTO savedDto = new BeerResponseDTO(saved);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();
            var resEntity = ResponseEntity.created(location).body(savedDto);
            return resEntity;
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
