package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.gui.config.ConfigProperties;
import com.demo.alkolicznik.security.config.SecurityVaadinConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("no-vaadin")
@PropertySource("classpath:profiles/no-vaadin.properties")
public class NoVaadinProfile {

    @MockBean
    SecurityVaadinConfig securityVaadinConfig;

    @MockBean
    ConfigProperties configProperties;
}
