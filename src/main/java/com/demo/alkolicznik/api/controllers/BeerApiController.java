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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BeerApiController {

    private BeerService beerService;

    public BeerApiController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping("/beer/{id}")
    public ResponseEntity<BeerResponseDTO> getBeer(@PathVariable Long id) {
        Beer beer = beerService.get(id);
        BeerResponseDTO beerDTO = new BeerResponseDTO(beer);
        return ResponseEntity.ok(beerDTO);
    }

    @GetMapping("/beer")
    public ResponseEntity<List<BeerResponseDTO>> getBeers(@RequestParam("city") String city) {
        List<Beer> beers = beerService.getBeers(city);
        List<BeerResponseDTO> beersDTO = beers.stream()
                .map(BeerResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersDTO);
    }

    @GetMapping("/beer-price")
    public ResponseEntity<List<BeerPriceResponseDTO>> getBeerPrices(@RequestParam("city") String city) {
        List<BeerPrice> beers = beerService.getBeerPrices(city);
        List<BeerPriceResponseDTO> beersResponse = beers.stream()
                .map(BeerPriceResponseDTO::new)
                .toList();
        return ResponseEntity.ok(beersResponse);
    }

    @PostMapping("/beer")
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
