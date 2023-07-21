package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.ImageService;
import com.demo.alkolicznik.dto.responses.ImageModelResponseDTO;
import com.demo.alkolicznik.models.ImageModel;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ImageController {

    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/beer/{beer_id}/image")
    @SecurityRequirement(name = "Basic Authentication")
    public ImageModelResponseDTO get(@PathVariable("beer_id") Long beerId) {
        return imageService.getBeerImage(beerId);
    }

    @PostMapping("/beer/image/{beer_id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    public ImageModel addBeerImageParam(@PathVariable("beer_id") Long beerId,
                                        @RequestParam("path") String path) {
        return null;
    }
}
