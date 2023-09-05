package com.demo.alkolicznik.config.profiles;

import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Profile("test-controller")
public class TestControllerProfile {

    public static final HttpStatusCode TEST_ENDPOINT_OK_CODE = HttpStatus.I_AM_A_TEAPOT;
    public static final String TEST_ENDPOINT_BODY = "Access granted, ";

    @GetMapping("/all")
    @PermitAll
    public ResponseEntity<String> anonymous() {
        return ResponseEntity
                .status(TEST_ENDPOINT_OK_CODE)
                .body(TEST_ENDPOINT_BODY + "anon!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> user() {
        return ResponseEntity
                .status(TEST_ENDPOINT_OK_CODE)
                .body(TEST_ENDPOINT_BODY + "user!");
    }

    @GetMapping("/accountant")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    public ResponseEntity<String> accountant() {
        return ResponseEntity
                .status(TEST_ENDPOINT_OK_CODE)
                .body(TEST_ENDPOINT_BODY + "accountant!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> admin() {
        return ResponseEntity
                .status(TEST_ENDPOINT_OK_CODE)
                .body(TEST_ENDPOINT_BODY + "admin!");
    }
}
