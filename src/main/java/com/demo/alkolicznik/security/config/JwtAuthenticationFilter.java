package com.demo.alkolicznik.security.config;

import java.io.IOException;

import com.demo.alkolicznik.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (SecurityContextHolder.getContext().getAuthentication() == null &&
				(authHeader == null || !authHeader.startsWith("Bearer "))) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = extractJWT(authHeader, auth);
		final String username = jwtService.extractUsername(jwt);
		if (username != null
			/*&& SecurityContextHolder.getContext().getAuthentication() == null*/) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtService.isTokenValid(jwt, userDetails)) {
				var authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails
						(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String extractJWT(String authHeader, Authentication authObject) {
		// PreAuthenticatedAuthenticationToken would be a cookie
		if (authObject instanceof PreAuthenticatedAuthenticationToken)
			return (String) authObject.getPrincipal();
			// and here's the in-header JWT
		else return authHeader.substring(7);
	}
}
