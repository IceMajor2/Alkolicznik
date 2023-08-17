package com.demo.alkolicznik.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationProviderImpl extends DaoAuthenticationProvider {

	@Autowired
	public AuthenticationProviderImpl(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		super.setUserDetailsService(userDetailsService);
		super.setPasswordEncoder(passwordEncoder);
	}
}
