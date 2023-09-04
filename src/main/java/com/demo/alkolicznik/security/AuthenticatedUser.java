package com.demo.alkolicznik.security;

import com.demo.alkolicznik.models.User;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {

	public static boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return !(auth instanceof AnonymousAuthenticationToken);
	}

	public static boolean isUser() {
		User user = getLoggedUserFromContext();
		if(user == null) return false;
		return user.isUser();
	}

	public static boolean hasAccountantRole() {
		User user = getLoggedUserFromContext();
		if(user == null) return false;
		return user.hasAccountantRole();
	}

	private static User getLoggedUserFromContext() {
		if(!isAuthenticated()) {
			return null;
		}
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
