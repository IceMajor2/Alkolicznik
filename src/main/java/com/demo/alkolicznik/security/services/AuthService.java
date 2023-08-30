package com.demo.alkolicznik.security.services;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.exceptions.classes.UserAlreadyExistsException;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    public AuthResponseDTO register(SignupRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder
                .encode(request.getPassword()));
        assignRoles(user);
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponseDTO(jwt);
    }

    private void assignRoles(User user) {
        if (userRepository.count() == 0) user.setRole(Roles.ADMIN);
        else user.setRole(Roles.USER);
    }

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername()).get();
        String jwt = jwtService.generateToken(user);
        return new AuthResponseDTO(jwt);
    }
}