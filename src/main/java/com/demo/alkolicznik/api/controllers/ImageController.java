package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ImageController {

	private StoreImageService storeImageService;

	private BeerImageService beerImageService;

	@GetMapping("/beer/{beer_id}/image")
	public ImageResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
		return beerImageService.get(beerId);
	}

	@GetMapping("/beer/image")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	public List<ImageResponseDTO> getAllBeerImages() {
		return beerImageService.getAll();
	}

	@PostMapping("/beer/{beer_id}/image")
	public ResponseEntity<BeerResponseDTO> addBeerImage(
			@PathVariable("beer_id") Long beerId,
			@RequestBody @Valid ImageRequestDTO request) {
		BeerResponseDTO response = beerImageService.add(beerId, request);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.getId())
				.toUri();
		return ResponseEntity
				.created(location)
				.body(response);
	}

	@DeleteMapping("/beer/{beer_id}/image")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteBeerImage(@PathVariable("beer_id") Long beerId) {
		return beerImageService.delete(beerId);
	}

	@GetMapping("/store/{store_id}/image")
	public ImageResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
		return storeImageService.get(storeId);
	}

	@GetMapping("/store/image")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	public List<ImageResponseDTO> getAllStoreImages() {
		return storeImageService.getAll();
	}

	@GetMapping(value = "/store/image", params = "name")
	public ImageResponseDTO getStoreImage(@RequestParam("name") String storeName) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.get(storeName);
	}

	@PostMapping("/store/image")
	public ResponseEntity<ImageResponseDTO> addStoreImage(
			@RequestParam("name") String storeName,
			@RequestBody @Valid ImageRequestDTO imageRequestDTO) {
		// space in a path is represented by '%20' string, thus
		// we need to replace it with actual space char to get a valid name
		storeName = storeName.replace("%20", " ");
		ImageResponseDTO response = storeImageService.add(storeName, imageRequestDTO);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.queryParam("name", storeName)
				.build()
				.toUri();
		return ResponseEntity
				.created(location)
				.body(response);
	}

	@PutMapping("/store/image")
	public ImageResponseDTO updateStoreImage(@RequestParam("name") String storeName,
			@RequestBody @Valid ImageRequestDTO imageRequestDTO) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.update(storeName, imageRequestDTO);
	}

	@DeleteMapping("/store/image")
	@PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteStoreImage(@RequestParam("name") String storeName) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.delete(storeName);
	}
}
