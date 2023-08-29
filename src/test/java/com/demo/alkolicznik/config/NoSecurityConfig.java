package com.demo.alkolicznik.config;

import com.demo.alkolicznik.security.config.RestAuthenticationEntryPoint;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Primary
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
@Profile("no-security")
public class NoSecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public NoSecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE - 8)
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(excHandler -> excHandler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(authenticationEntryPoint))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
