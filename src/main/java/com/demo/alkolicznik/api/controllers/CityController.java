package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.city.CityDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
@Tag(name = "City")
public class CityController {

    private final StoreService storeService;

    @Operation(
            summary = "Get a list of all cities",
            description = "Each authenticated user is free to know all the currently-tracked cities in the application.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "City list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Unauthorized (dummy response)",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(
                    name = "JWT Authentication"
            )
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CityDTO> getAll() {
        return storeService.getAllCities();
    }
}
