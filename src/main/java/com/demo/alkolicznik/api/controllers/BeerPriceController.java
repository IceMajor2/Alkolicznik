package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;

import com.demo.alkolicznik.api.services.BeerPriceService;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Beer Price Controller")
public class BeerPriceController {

	private BeerPriceService beerPriceService;

	public BeerPriceController(BeerPriceService beerPriceService) {
		this.beerPriceService = beerPriceService;
	}

	@GetMapping(value = "/beer-price", params = { "store_id", "beer_id" })
	@Operation(summary = "Get the price of beer in a store",
			description = "Here's the right place to seek discounts!<br>"
					+ "<b>Options available:</b><br>"
					+ "<i>/api/beer-price</i> - lists everything: secured<br>"
					+ "<i>/api/beer-price?store_id=?beer_id=?</i> - specific price<br>"
					+ "<i>/api/beer-price?city=?</i> - all prices from stores in a specified city<br>")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "price retrieved"),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "beer not found", content = @Content),
			@ApiResponse(responseCode = "404 (3)", description = "store not found", content = @Content),
			@ApiResponse(responseCode = "404 (4)", description = "both store and beer not found", content = @Content)
	})
	public BeerPriceResponseDTO get(
			@RequestParam(value = "store_id", required = false) Long storeId,
			@RequestParam(value = "beer_id", required = false) Long beerId) {
		return beerPriceService.get(storeId, beerId);
	}

	@GetMapping("/beer-price")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "Basic Authentication")
	public List<BeerPriceResponseDTO> getAll() {
		return beerPriceService.getAll();
	}

	@GetMapping(value = "/beer-price", params = "city")
	public List<BeerPriceResponseDTO> getAllByCity(
			@RequestParam(value = "city", required = false) String city) {
		return beerPriceService.getAllByCity(city);
	}

	@GetMapping("/store/{store_id}/beer-price")
	public List<BeerPriceResponseDTO> getAllByStoreId(@PathVariable("store_id") Long storeId) {
		return beerPriceService.getAllByStoreId(storeId);
	}

	@GetMapping("/beer/{beer_id}/beer-price")
	public List<BeerPriceResponseDTO> getAllByBeerId(@PathVariable("beer_id") Long beerId) {
		return beerPriceService.getAllByBeerId(beerId);
	}

	@GetMapping(value = "/beer/{beer_id}/beer-price", params = "city")
	public List<BeerPriceResponseDTO> getAllByBeerIdAndCity(@PathVariable("beer_id") Long beerId,
			@RequestParam("city") String city) {
		return beerPriceService.getAllByBeerIdAndCity(beerId, city);
	}

	@PostMapping("/store/{store_id}/beer-price")
	@SecurityRequirement(name = "Basic Authentication")
	public ResponseEntity<BeerPriceResponseDTO> addByObject(
			@PathVariable("store_id") Long storeId,
			@RequestBody @Valid BeerPriceRequestDTO beerPriceRequestDTO) {
		BeerPriceResponseDTO beerPrice = beerPriceService.addByObject(storeId, beerPriceRequestDTO);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(beerPrice.getBeer().getId())
				.toUri();
		return ResponseEntity
				.created(location)
				.body(beerPrice);
	}

	@PostMapping(value = "/store/{store_id}/beer-price", params = { "beer_id", "beer_price" })
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "Basic Authentication")
	public ResponseEntity<BeerPriceResponseDTO> addByParam(
			@PathVariable("store_id") Long storeId,
			@RequestParam(value = "beer_id") Long beerId,
			@RequestParam(value = "beer_price")
			@Positive(message = "Price must be a positive number") Double price) {
		BeerPriceResponseDTO beerPrice = beerPriceService.addByParam(storeId, beerId, price);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(beerPrice.getBeer().getId())
				.toUri();
		return ResponseEntity
				.created(location)
				.body(beerPrice);
	}

	@PatchMapping(value = "/beer-price", params = { "store_id", "beer_id", "price" })
	@SecurityRequirement(name = "Basic Authentication")
	public BeerPriceResponseDTO update(@RequestParam("store_id") Long storeId,
			@RequestParam("beer_id") Long beerId,
			@RequestParam("price") @Positive(message = "Price must be a positive number") Double price) {
		return beerPriceService.update(storeId, beerId, price);
	}

	@DeleteMapping("/beer-price")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "Basic Authentication")
	public BeerPriceDeleteDTO delete(@RequestParam("store_id") Long storeId,
			@RequestParam("beer_id") Long beerId) {
		return beerPriceService.delete(storeId, beerId);
	}
}
