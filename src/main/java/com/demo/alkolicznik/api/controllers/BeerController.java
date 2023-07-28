package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;

import com.demo.alkolicznik.api.services.BeerService;
import com.demo.alkolicznik.dto.beer.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
	@Operation(summary = "Get beer details",
			description = "Include id of beer you would like to see details of. "
					+ "Details include: see response example below.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "beer details retrieved"),
			@ApiResponse(responseCode = "404", description = "beer not found", content = @Content)
	})
	public BeerResponseDTO get(@PathVariable("beer_id") Long id) {
		return beerService.get(id);
	}

	@GetMapping(params = "city")
	@Operation(summary = "Get a list of currently tracked beers",
			description = "Average user is only enabled to get an array of beers from a "
					+ "desired city. Accountants may retrieve all beers from database "
					+ "simply by omitting the 'city' parameter.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "beer list retrieved"),
			@ApiResponse(responseCode = "404", description = "city not found", content = @Content)
	})
	public List<BeerResponseDTO> getAllInCity(@RequestParam(value = "city", required = false) String city) {
		return beerService.getBeers(city);
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "Basic Authentication")
	public List<BeerResponseDTO> getAll() {
		return beerService.getBeers();
	}

	@Operation(summary = "Add new beer",
			description = "If you found a beer missing, feel free to add it")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "beer successfully created"),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "409", description = "such beer already exists", content = @Content)
	})
	@PostMapping
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

	@Operation(summary = "Replace beer",
			description = "Here you can replace an already existing beer with new one. "
					+ "Features? You can keep the id! How cool is that? "
					+ "WARNING: every price associated with the previous beer "
					+ "will be deleted!")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "beer successfully replaced"),
			@ApiResponse(responseCode = "200 (2)", description = "replacement is the same as original entity - nothing happens", content = @Content),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "beer of provided id was not found", content = @Content),
			@ApiResponse(responseCode = "409", description = "such beer already exists", content = @Content)
	})
	@PutMapping("/{beer_id}")
	@SecurityRequirement(name = "Basic Authentication")
	public BeerResponseDTO replace(@PathVariable("beer_id") Long beerId,
			@RequestBody @Valid BeerRequestDTO requestDTO) {
		return beerService.replace(beerId, requestDTO);
	}

	@Operation(summary = "Update beer",
			description = "If you are interested in updating just one or two - or even more - individual "
					+ "fields, you've come to the right place. However, please be cautious "
					+ "as updating beer's brand and/or type will also remove its image "
					+ "(if there had already been one) and remove it from all the stores. "
					+ "WARNING #1: If you update brand and/or type, the beer will be removed "
					+ "from each store it has been previously linked to. "
					+ "WARNING #2: If you update anything else than beer volume, then "
					+ "beer image will be deleted.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "beer successfully updated"),
			@ApiResponse(responseCode = "200", description = "replacement is the same as original entity - nothing happens", content = @Content),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "400 (2)", description = "there was not one single property to update specified", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "beer of provided id was not found", content = @Content),
			@ApiResponse(responseCode = "409", description = "such beer already exists", content = @Content)
	})
	@PatchMapping("/{beer_id}")
	@SecurityRequirement(name = "Basic Authentication")
	public BeerResponseDTO update(@PathVariable("beer_id") Long beerId,
			@RequestBody @Valid BeerUpdateDTO updateDTO) {
		return beerService.update(beerId, updateDTO);
	}

	@Operation(summary = "Delete beer by id",
			description = "Are you tired of selling a certain beer? You can't make "
					+ "enough money or working with the provider is a pain in the ass? "
					+ "Say no more! Just give me an ID...")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "beer successfully deleted"),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "beer of provided id was not found", content = @Content)
	})
	@DeleteMapping("/{beer_id}")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "Basic Authentication")
	public BeerDeleteResponseDTO delete(@PathVariable("beer_id") Long beerId) {
		return beerService.delete(beerId);
	}

	@Operation(summary = "Delete beer by properties",
			description = "You can't sell a beer and - even worse - you can't get its ID? "
					+ "Not a problem! Try to describe it just as you'd create it de novo")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "beer successfully deleted", content = @Content),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "beer of provided details was not found", content = @Content)
	})
	@DeleteMapping
	@SecurityRequirement(name = "Basic Authentication")
	public BeerDeleteResponseDTO delete(@RequestBody @Valid BeerDeleteRequestDTO requestDTO) {
		return beerService.delete(requestDTO);
	}
}
