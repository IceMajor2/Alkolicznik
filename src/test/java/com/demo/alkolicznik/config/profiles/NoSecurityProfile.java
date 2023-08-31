package com.demo.alkolicznik.config.profiles;

import org.junit.jupiter.api.Order;
import org.springframework.context.annotation.*;
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
@PropertySource("classpath:profiles/no-security.properties")
public class NoSecurityProfile {

    @Bean
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE - 8)
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
