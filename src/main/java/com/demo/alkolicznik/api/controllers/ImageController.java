package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.ImageService;
import com.demo.alkolicznik.dto.responses.ImageResponseDTO;
import com.demo.alkolicznik.models.Image;
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
    public ImageResponseDTO get(@PathVariable("beer_id") Long beerId) {
        return imageService.getBeerImage(beerId);
    }

    @PostMapping("/beer/image/{beer_id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ACCOUNTANT')")
    public Image addBeerImageParam(@PathVariable("beer_id") Long beerId,
                                   @RequestParam("path") String path) {
        return null;
    }
}
