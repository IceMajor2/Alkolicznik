package com.demo.alkolicznik.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

	public boolean authenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return !(auth instanceof AnonymousAuthenticationToken);
	}
}
