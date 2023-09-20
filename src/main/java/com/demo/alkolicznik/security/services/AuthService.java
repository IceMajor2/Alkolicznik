package com.demo.alkolicznik.security.services;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.security.SignupResponseDTO;
import com.demo.alkolicznik.exceptions.classes.user.UserAlreadyExistsException;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Transactional
    public SignupResponseDTO register(SignupRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder
                .encode(request.getPassword()));
        assignRoles(user);
        User saved = userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new SignupResponseDTO(saved, jwt);
    }

    private void assignRoles(User user) {
        if (userRepository.count() == 0) user.setRole(Roles.ADMIN);
        else user.setRole(Roles.USER);
    }

    @Transactional(readOnly = true)
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
