package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.exceptions.classes.UserAlreadyExistsException;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.repositories.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationContext authenticationContext;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

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

	public UserDetails getAuthenticatedUser() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class).orElse(null);
	}

	public void logout() {
		authenticationContext.logout();
	}

	private void assignRoles(User user) {
		if (userRepository.count() == 0) user.setRole(Roles.ADMIN);
		else user.setRole(Roles.USER);
	}
}
