package com.demo.alkolicznik.api.controllers;

import java.net.URI;

import com.demo.alkolicznik.api.services.ImageService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ImageController {

	private ImageService imageService;

	@GetMapping("/beer/{beer_id}/image")
	public ImageModelResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
		return imageService.getBeerImage(beerId);
	}

	@DeleteMapping("/beer/{beer_id}/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteBeerImage(@PathVariable("beer_id") Long beerId) {
		return imageService.delete(beerId);
	}

	@GetMapping("/store/{store_id}/image")
	public ImageModelResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
		return imageService.getStoreImage(storeId);
	}

	@GetMapping("/image")
	public ImageModelResponseDTO getStoreImage(@RequestParam("store_name") String storeName) {
		return imageService.getStoreImage(storeName);
	}

	@PostMapping("/image")
	public ResponseEntity<ImageModelResponseDTO> addStoreImage
			(@RequestParam("store_name") String storeName,
					@RequestBody @Valid ImageRequestDTO imageRequestDTO) {
		// space in a path is represented by '%20' string, thus
		// we need to replace it with actual space char to get a valid name
		storeName = storeName.replace("%20", " ");
		ImageModelResponseDTO
				response = imageService.addStoreImage(storeName, imageRequestDTO);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.queryParam("store_name", storeName)
				.build()
				.toUri();
		return ResponseEntity
				.created(location)
				.body(response);
	}
}
