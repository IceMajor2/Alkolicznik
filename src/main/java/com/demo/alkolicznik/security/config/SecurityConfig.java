package com.demo.alkolicznik.security.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final RestAuthenticationEntryPoint authenticationEntryPoint;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final CookieAuthenticationFilter cookieAuthenticationFilter;

	private final AuthenticationProvider authenticationProvider;

	private static final String[] ACCOUNTANT_AUTHORITIES = new String[] { "ADMIN", "ACCOUNTANT" };

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE + 8)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.exceptionHandling(excHandler -> excHandler
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(authenticationEntryPoint))
				.formLogin((form) -> form
						.loginPage("/login")
						.permitAll())
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/error").permitAll()
						// secured endpoints that request a valid body
						// needs to be specified below
						.requestMatchers(HttpMethod.POST, "/api/beer").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/beer/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.DELETE, "/api/beer").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/beer").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/beer/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/store/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/store").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/store/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/store/*/beer-price").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/beer-price/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/store/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/store/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/beer/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/beer/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.anyRequest().permitAll())
				.sessionManagement(sessionManager -> sessionManager
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(cookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
