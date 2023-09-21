package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.store.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/store")
@Tag(name = "Store")
public class StoreController {

	private StoreService storeService;

	public StoreController(StoreService storeService) {
		this.storeService = storeService;
	}

	@GetMapping("/{store_id}")
	@Operation(summary = "Get store details",
			description = "Include id of store you would like to see details of.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "store details retrieved"),
			@ApiResponse(responseCode = "404", description = "store not found", content = @Content)
	})
	public StoreResponseDTO get(@PathVariable("store_id") Long id) {
		return storeService.get(id);
	}

	@GetMapping(params = "city")
	@Operation(summary = "Get a list of currently tracked stores",
			description = "Average user is only enabled to get an array of stores from a "
					+ "desired city. Accountants may retrieve all stores from database.<br>"
					+ "<b>Options available</b>:<br>"
					+ "<i>/api/store</i> - lists all stores in database: secured<br>"
					+ "<i>/api/store?city=</i> - lists all stores in a city")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "store list retrieved"),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "city not found", content = @Content)
	})
	public List<StoreResponseDTO> getAllInCity(@RequestParam(value = "city", required = false) String city) {
		return storeService.getStores(city);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "JWT Authentication")
	public List<StoreResponseDTO> getAll() {
		return storeService.getStores();
	}

	@GetMapping(params = "brand_only")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	public List<StoreNameDTO> getAllBrands(@RequestParam("brand_only") Object brandOnly) {
		return storeService.getAllBrands();
	}

	@Operation(summary = "Add new store",
			description = "Hey, if you just opened up a new store, "
					+ "do not hesitate to tell us so!")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "store successfully created"),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "409", description = "such store already exists", content = @Content)
	})
	@PostMapping
	@SecurityRequirement(name = "JWT Authentication")
	public ResponseEntity<StoreResponseDTO> add(@RequestBody @Valid StoreRequestDTO storeRequestDTO) {
		StoreResponseDTO saved = storeService.add(storeRequestDTO);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(saved.getId())
				.toUri();
		return ResponseEntity
				.created(location)
				.body(saved);
	}

	@Operation(summary = "Replace store",
			description = "Replace a store that, for example, you might have seen "
					+ "closed and replaced by a new one.<br>"
					+ "Features? You can keep the id!<br>"
					+ "<b>WARNING:</b> every price associated with the previous store "
					+ "will be deleted!")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "store successfully replaced"),
			@ApiResponse(responseCode = "200 (2)", description = "replacement is the same as original entity - nothing happens", content = @Content),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "store not found", content = @Content),
			@ApiResponse(responseCode = "409", description = "such store already exists", content = @Content)
	})
	@PutMapping("/{store_id}")
	@SecurityRequirement(name = "JWT Authentication")
	public StoreResponseDTO replace(@PathVariable("store_id") Long storeId,
			@RequestBody @Valid StoreRequestDTO requestDTO) {
		return storeService.replace(storeId, requestDTO);
	}

	@Operation(summary = "Update store",
			description = "If you'd just like to tweak some store's properties, "
					+ "without fully providing a new set of data, then here is "
					+ "the right place to do so.<br>"
					+ "<b>WARNING:</b> No matter what field you replace, "
					+ "all of the store prices will, of course, be deleted!")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "store successfully updated"),
			@ApiResponse(responseCode = "200 (2)", description = "replacement is the same as original entity - nothing happens", content = @Content),
			@ApiResponse(responseCode = "400", description = "provided data violates constraints", content = @Content),
			@ApiResponse(responseCode = "400 (2)", description = "there was not one single property to update specified in the request", content = @Content),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "store not found", content = @Content),
			@ApiResponse(responseCode = "409", description = "such store already exists", content = @Content)
	})
	@PatchMapping("/{store_id}")
	@SecurityRequirement(name = "JWT Authentication")
	public StoreResponseDTO update(@PathVariable("store_id") Long storeId,
			@RequestBody @Valid StoreUpdateDTO updateDTO) {
		return storeService.update(storeId, updateDTO);
	}

	@Operation(summary = "Delete store by id",
			description = "You've just gone bankrupt... again, haven't you?"
					+ " Well, what a shame...<br>"
					+ "But please, do remember to delete it from <b>Alkolicznik</b>"
					+ '\u2122' + "... Thanks!")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "store successfully deleted"),
			@ApiResponse(responseCode = "404", description = "resource not found - dummy response "
					+ "(when unauthorized/unauthenticated user tries to fetch resources)", content = @Content),
			@ApiResponse(responseCode = "404 (2)", description = "store not found", content = @Content)
	})
	@DeleteMapping("/{store_id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	@SecurityRequirement(name = "JWT Authentication")
	public StoreDeleteDTO deleteByParam(@PathVariable("store_id") Long storeId) {
		return storeService.delete(storeId);
	}

	@DeleteMapping
	public StoreDeleteDTO deleteByObject(@RequestBody @Valid StoreRequestDTO request) {
		return storeService.delete(request);
	}
}
