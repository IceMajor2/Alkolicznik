package com.demo.alkolicznik.config.profiles;

import com.demo.alkolicznik.security.config.SecurityVaadinConfig;
import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;
import com.vaadin.flow.spring.VaadinScopesConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAutoConfiguration(exclude = {
        VaadinScopesConfig.class,
        SpringBootAutoConfiguration.class,
        SpringSecurityAutoConfiguration.class,
})
@Profile("no-vaadin")
public class NoVaadinProfile {

    @MockBean
    SecurityVaadinConfig securityVaadinConfig;
}
