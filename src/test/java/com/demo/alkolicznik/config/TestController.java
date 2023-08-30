package com.demo.alkolicznik.config;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Profile("test-endpoints")
public class TestController {
    public TestController() {
        System.out.println("hi");
    }

    @GetMapping("/all")
    @PermitAll
    public ResponseEntity<String> anonymous() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Access permitted, anon!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> user() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Access permitted, user!");
    }

    @GetMapping("/accountant")
    @PreAuthorize("hasAuthority('ACCOUNTANT')")
    public ResponseEntity<String> accountant() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Access permitted, accountant!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> admin() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Access permitted, admin!");
    }
}
