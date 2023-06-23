package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.api.services.BeerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BeerController {

    private BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Fetch beer from database.
     * @param id beer id passed in path
     * @return Beer object wrapped in ResponseEntity class
     */
    @GetMapping("/beer/{id}")
    public ResponseEntity<BeerResponseDTO> getBeer(@PathVariable Long id) {
        Beer beer = beerService.get(id);
        if(beer == null) {
            return ResponseEntity.notFound().build();
        }
        BeerResponseDTO beerDto = new BeerResponseDTO(beer);
        return ResponseEntity.ok(beerDto);
    }

    /**
     * Add new beer to database.
     *
     * @param beerRequestDTO Beer body
     * @return updated by database Beer object wrapped in ResponseEntity class
     */
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
        } catch(RuntimeException e) {
            throw e;
        }
    }

    @GetMapping("/beer")
    public ResponseEntity<List<BeerResponseDTO>> getBeers() {
        List<Beer> beers = beerService.getBeers();
        List<BeerResponseDTO> beersDto = beers.stream()
                .map(beerService::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beersDto);
    }
}
