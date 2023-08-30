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

    private static final String WELCOME_MESSAGE_START = "Access granted, ";

    @GetMapping("/all")
    @PermitAll
    public ResponseEntity<String> anonymous() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(WELCOME_MESSAGE_START + "anon!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> user() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(WELCOME_MESSAGE_START + "user!");
    }

    @GetMapping("/accountant")
    @PreAuthorize("hasAuthority('ACCOUNTANT')")
    public ResponseEntity<String> accountant() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(WELCOME_MESSAGE_START + "accountant!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> admin() {
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(WELCOME_MESSAGE_START + "admin!");
    }
}
