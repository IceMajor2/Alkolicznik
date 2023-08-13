package com.demo.alkolicznik.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] ACCOUNTANT_AUTHORITIES = new String[]{"ADMIN", "ACCOUNTANT"};

    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 8)
    @Bean
    public SecurityFilterChain restApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic((httpBasic) -> httpBasic
                        .authenticationEntryPoint(authenticationEntryPoint))
                // or withDefaults()???
                .exceptionHandling(excHandler -> excHandler
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(authenticationEntryPoint))
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.PUT, "/api/beer/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.DELETE, "/api/beer").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/beer").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/beer/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/store/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/store").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/store/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
                        .requestMatchers(HttpMethod.POST, "/api/store/*/beer-price").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/beer-price/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.POST, "/api/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PUT, "/api/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
						.requestMatchers(HttpMethod.PATCH, "/api/image/*").hasAnyAuthority(ACCOUNTANT_AUTHORITIES)
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
