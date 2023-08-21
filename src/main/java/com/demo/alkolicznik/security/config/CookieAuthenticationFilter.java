package com.demo.alkolicznik.security.config;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {

	private static final String JWT_COOKIE_NAME = "token";

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		Optional<Cookie> authCookie = Stream.of(Optional.ofNullable(request.getCookies())
						.orElse(new Cookie[0]))
				.filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
				.findFirst();
		authCookie.ifPresent(cookie ->
				SecurityContextHolder.getContext().setAuthentication(
						new PreAuthenticatedAuthenticationToken(cookie.getValue(), null)
				));
		filterChain.doFilter(request, response);
	}
}
