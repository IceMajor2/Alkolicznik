package com.demo.alkolicznik.security.controllers;

import com.demo.alkolicznik.dto.security.*;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.security.services.AuthService;
import com.demo.alkolicznik.utils.request.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            description = "Create account for this application." +
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
                            description = "Account was registered",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SignupResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Username is taken",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody @Valid SignupRequestDTO request) {
        SignupResponseDTO response = authService.register(request);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri()
        ).body(response);
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Log in",
            description = "This is a necessary step in order to access all the goods of <b>Alkolicznik&trade;</b>.<br>" +
                    "<b>NOTE:</b> successful authentication automatically sets cookie with token " +
                    "so you don't need to log in on each request.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"username\":\"your_username\",\"password\":\"your_password\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "You have logged in",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthRequestDTO.class)
                            ),
                            headers = @Header(
                                    name = "Set-Cookie",
                                    description = "sets cookie with JWT"
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid credentials",
                            content = @Content
                    )
            }
    )
    public AuthResponseDTO authenticate(@RequestBody @Valid AuthRequestDTO request,
                                        HttpServletResponse response) {
        AuthResponseDTO tokenDTO = authService.authenticate(request);
        response.addCookie(CookieUtils.createTokenCookie(tokenDTO.getToken()));
        return tokenDTO;
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out",
            description = "Take care... and come back soon!",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "You have been logged out",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    headers = @Header(
                            name = "Set-Cookie",
                            description = "removes (if present) cookie with JWT"
                    )
            )
    )
    public ResponseEntity<AuthLogoutDTO> logout(@AuthenticationPrincipal User user,
                                                HttpServletRequest request, HttpServletResponse response) {
        Cookie expiredAuthCookie = authService.getLogoutCookie(user, request);
        response.addCookie(expiredAuthCookie);
        return ResponseEntity.ok(new AuthLogoutDTO());
    }
}
