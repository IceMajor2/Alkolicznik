package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.BeerDeleteDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public BeerResponseDTO get(@PathVariable("beer_id") Long id) {
        return beerService.get(id);
    }

    @GetMapping(params = "city")
    public List<BeerResponseDTO> getAllInCity(@RequestParam("city") String city) {
        return beerService.getBeers(city);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public List<BeerResponseDTO> getAll() {
        return beerService.getBeers();
    }

    @PostMapping
    // secured in SecurityConfig
    @SecurityRequirement(name = "Basic Authentication")
    public ResponseEntity<BeerResponseDTO> add(@RequestBody @Valid BeerRequestDTO beerRequestDTO) {
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

    @PutMapping("/{beer_id}")
    // secured in SecurityConfig
    @SecurityRequirement(name = "Basic Authentication")
    public BeerResponseDTO replace(@PathVariable("beer_id") Long beerId,
                                                  @RequestBody @Valid BeerRequestDTO requestDTO) {
        return beerService.replace(beerId, requestDTO);
    }

    @PatchMapping("/{beer_id}")
    public BeerResponseDTO update(@PathVariable("beer_id") Long beerId,
                                  @RequestBody @Valid BeerUpdateDTO updateDTO) {
        return beerService.update(beerId, updateDTO);
    }

    @DeleteMapping("/{beer_id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    @SecurityRequirement(name = "Basic Authentication")
    public BeerDeleteDTO delete(@PathVariable("beer_id") Long beerId) {
        return beerService.delete(beerId);
    }

    @DeleteMapping
    // secured in SecurityConfig
    @SecurityRequirement(name = "Basic Authentication")
    public BeerDeleteDTO delete(@RequestBody @Valid BeerRequestDTO requestDTO) {
        return beerService.delete(requestDTO);
    }
}
