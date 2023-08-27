package com.demo.alkolicznik.config;

import com.demo.alkolicznik.security.AuthController;
import com.demo.alkolicznik.security.AuthService;
import com.demo.alkolicznik.security.config.SecurityVaadinConfig;
import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;
import com.vaadin.flow.spring.VaadinScopesConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

@EnableAutoConfiguration(exclude = {
        VaadinScopesConfig.class,
        SpringBootAutoConfiguration.class,
        SpringSecurityAutoConfiguration.class
})
public class DisabledVaadinContext {

    @MockBean
    private AuthController authController;

    @MockBean
    private AuthService authService;

    @MockBean
    private SecurityVaadinConfig securityVaadinConfig;

    @MockBean
    private AuthenticationManager authenticationManager;
}
