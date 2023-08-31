package com.demo.alkolicznik.security.controllers;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.security.SignupResponseDTO;
import com.demo.alkolicznik.security.services.AuthService;
import com.demo.alkolicznik.utils.request.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
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
}
