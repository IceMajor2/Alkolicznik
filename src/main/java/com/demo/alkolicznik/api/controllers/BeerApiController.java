package com.demo.alkolicznik.api.controllers;

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

@RestController
@RequestMapping("/api/beer")
public class BeerApiController {

    private BeerService beerService;

    public BeerApiController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerResponseDTO> getBeer(@PathVariable Long id, @RequestParam String city) {
        Beer beer = beerService.get(id);
        BeerResponseDTO beerDto = new BeerResponseDTO(beer);
        return ResponseEntity.ok(beerDto);
    }

    @GetMapping
    public ResponseEntity<List<BeerPrice>> getBeers(@RequestParam("city") String city) {
        return ResponseEntity.ok(beerService.getBeers(city));
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
        } catch(RuntimeException e) {
            throw e;
        }
    }
}
