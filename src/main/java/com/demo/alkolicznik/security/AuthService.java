package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.requests.UserRequestDTO;
import com.demo.alkolicznik.exceptions.classes.UserAlreadyExistsException;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.repositories.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationContext authenticationContext;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationContext authenticationContext,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRequestDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException();
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User user = new User(userDTO);
        assignRole(user);

        User saved = userRepository.save(user);
        return saved;
    }

    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElse(null);
    }

    public void logout() {
        authenticationContext.logout();
    }

    private void assignRole(User user) {
        if (userRepository.count() == 0) {
            user.getRoles().add(Roles.ADMIN);
            return;
        }
        user.getRoles().add(Roles.USER);
    }
}
