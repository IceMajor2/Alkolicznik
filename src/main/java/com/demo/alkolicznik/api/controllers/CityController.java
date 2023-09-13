package com.demo.alkolicznik.api.controllers;

import com.demo.alkolicznik.api.services.StoreService;
import com.demo.alkolicznik.dto.city.CityDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@Tag(name = "City Controller")
@RequiredArgsConstructor
public class CityController {

    private final StoreService storeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CityDTO> getAll() {
        return storeService.getAllCities();
    }
}
