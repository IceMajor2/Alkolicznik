package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/beer")
public class BeerController {

    private BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping("/{beer_id}")
    public BeerResponseDTO getBeer(@PathVariable("beer_id") Long id) {
        return beerService.get(id);
    }

    @GetMapping
    public List<BeerResponseDTO> getBeers(@RequestParam("city") String city) {
        return beerService.getBeers(city);
    }

    @PostMapping
    public ResponseEntity<BeerResponseDTO> addBeer(@RequestBody @Valid BeerRequestDTO beerRequestDTO) {
        BeerResponseDTO savedDTO = beerService.add(beerRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDTO.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(savedDTO);
    }
}
