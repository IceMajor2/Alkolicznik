package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.ImageService;
import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/beer/{beer_id}/image")
    public ImageModelResponseDTO getBeerImage(@PathVariable("beer_id") Long beerId) {
        return imageService.getBeerImage(beerId);
    }

	@DeleteMapping("/beer/{beer_id}/image")
	@PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
	public ImageDeleteDTO deleteBeerImage(@PathVariable("beer_id") Long beerId) {
		return imageService.delete(beerId);
	}

	@GetMapping("store/{store_id}/image")
	public ImageModelResponseDTO getStoreImage(@PathVariable("store_id") Long storeId) {
		return imageService.getStoreImage(storeId);
	}
}
