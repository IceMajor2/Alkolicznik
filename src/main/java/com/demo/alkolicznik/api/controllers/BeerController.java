package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.BeerDeleteDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/beer")
@Tag(name = "Beer Controller")
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
	@Operation(summary = "Get a list of currently tracked beers",
			description = "Average user is only enabled to get an array of beers from a "
					+ "desired city. Accountants may retrieve all beers from database "
					+ "simply by omitting the 'city' parameter.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
			@ApiResponse(responseCode = "404", description = "Not found - no such city")
	})
	public List<BeerResponseDTO> getAllInCity(@RequestParam("city") @Parameter(required = false) String city) {
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
	@SecurityRequirement(name = "Basic Authentication")
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
