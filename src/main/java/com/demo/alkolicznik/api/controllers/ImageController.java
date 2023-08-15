package com.demo.alkolicznik.api.controllers;

import java.net.URI;
import java.util.List;

import com.demo.alkolicznik.api.services.BeerImageService;
import com.demo.alkolicznik.api.services.StoreImageService;
import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ImageController {

	private StoreImageService storeImageService;

	private BeerImageService beerImageService;

	@GetMapping("/beer/{beer_id}/image")
	public ImageModelResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
		return beerImageService.get(beerId);
	}

	@GetMapping("/beer/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public List<ImageModelResponseDTO> getAllBeerImages() {
		return beerImageService.getAll();
	}

	@PostMapping("/beer/{beer_id}/image")
	public ImageModelResponseDTO addBeerImage(@PathVariable("beer_id") Long beerId,
			@RequestBody @Valid ImageRequestDTO request) {
		return null;
	}

	@DeleteMapping("/beer/{beer_id}/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteBeerImage(@PathVariable("beer_id") Long beerId) {
		return beerImageService.delete(beerId);
	}

	@GetMapping("/store/{store_id}/image")
	public ImageModelResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
		return storeImageService.get(storeId);
	}

	@GetMapping("/store/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public List<ImageModelResponseDTO> getAllStoreImages() {
		return storeImageService.getAll();
	}

	@GetMapping(value = "/store/image", params = "name")
	public ImageModelResponseDTO getStoreImage(@RequestParam("name") String storeName) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.get(storeName);
	}

	@PostMapping("/store/image")
	public ResponseEntity<ImageModelResponseDTO> addStoreImage(
			@RequestParam("name") String storeName,
			@RequestBody @Valid ImageRequestDTO imageRequestDTO) {
		// space in a path is represented by '%20' string, thus
		// we need to replace it with actual space char to get a valid name
		storeName = storeName.replace("%20", " ");
		ImageModelResponseDTO response = storeImageService.add(storeName, imageRequestDTO);
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
	public ImageModelResponseDTO updateStoreImage(@RequestParam("name") String storeName,
			@RequestBody @Valid ImageRequestDTO imageRequestDTO) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.update(storeName, imageRequestDTO);
	}

	@DeleteMapping("/store/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteStoreImage(@RequestParam("name") String storeName) {
		storeName = storeName.replace("%20", " ");
		return storeImageService.delete(storeName);
	}
}
