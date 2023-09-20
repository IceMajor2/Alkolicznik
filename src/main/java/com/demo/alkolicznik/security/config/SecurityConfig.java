package com.demo.alkolicznik.security.config;

import com.demo.alkolicznik.exceptions.config.CustomExceptionHandler;
import com.demo.alkolicznik.security.filters.CookieAuthenticationFilter;
import com.demo.alkolicznik.security.filters.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "security", value = "config.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CookieAuthenticationFilter cookieAuthenticationFilter;
    private final CustomExceptionHandler.ExceptionHandlerFilter exceptionHandlerFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] ACCOUNTANT_AUTHORITIES = new String[]{"ADMIN", "ACCOUNTANT"};

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE - 8)
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
                        .requestMatchers(HttpMethod.POST, "/api/beer").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PUT, "/api/beer/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.DELETE, "/api/beer").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/beer").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PATCH, "/api/beer/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PUT, "/api/store/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/store").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PATCH, "/api/store/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/store/*/beer-price").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PATCH, "/api/beer-price/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/store/image/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PUT, "/api/store/image/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/beer/image/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.PUT, "/api/beer/image/*").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.DELETE, "/api/store").hasAnyRole(ACCOUNTANT_AUTHORITIES)
                        .anyRequest().permitAll())
                .sessionManagement(sessionManager -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(exceptionHandlerFilter, CorsFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cookieAuthenticationFilter, JwtAuthenticationFilter.class);
        return http.build();
    }
}
