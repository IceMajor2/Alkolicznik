package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.dto.user.UserRequestDTO;
import com.demo.alkolicznik.dto.user.UserResponseDTO;
import com.demo.alkolicznik.models.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO userDTO) {
        User saved = authService.registerUser(userDTO);
        UserResponseDTO responseDTO = new UserResponseDTO(saved);
        return ResponseEntity.created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(saved.getId())
                        .toUri())
                .body(responseDTO);
    }
}
