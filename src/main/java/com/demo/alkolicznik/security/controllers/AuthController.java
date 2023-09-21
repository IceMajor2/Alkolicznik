package com.demo.alkolicznik.security.controllers;

import com.demo.alkolicznik.dto.security.*;
import com.demo.alkolicznik.security.services.AuthService;
import com.demo.alkolicznik.utils.request.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Account")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(
            summary = "Register an account",
            description =
                    "Create account for this application." +
                    "<br><b>CONSTRAINTS:</b><br>" +
                    "- password must be at least 12 characters long<br>" +
                    "- username must not exist in database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"username\":\"john\",\"password\":\"twelve_chars_long\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "account was registered",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SignupResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "validation failed",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "username is taken",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<SignupResponseDTO> signup
            (@RequestBody @Valid SignupRequestDTO request) {
        SignupResponseDTO response = authService.register(request);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri()
        ).body(response);
    }

    @PostMapping("/authenticate")
    public AuthResponseDTO authenticate(@RequestBody @Valid AuthRequestDTO request,
                                        HttpServletResponse response) {
        AuthResponseDTO tokenDTO = authService.authenticate(request);
        response.addCookie(CookieUtils.createTokenCookie(tokenDTO.getToken()));
        return tokenDTO;
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthLogoutDTO> logout(HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(CookieUtils.createExpiredTokenCookie(request));
        return ResponseEntity.ok(new AuthLogoutDTO());
    }
}
