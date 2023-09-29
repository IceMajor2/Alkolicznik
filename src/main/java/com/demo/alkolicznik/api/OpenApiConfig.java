package com.demo.alkolicznik.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    @Profile("!alwaysdata")
    public OpenAPI openAPI() {
        return defaultOpenAPIDefinition();
    }

    @Bean
    @Profile("alwaysdata")
    public OpenAPI OpenAPI(@Value("${server.domain}") String domain) {
        return defaultOpenAPIDefinition()
                .servers(List.of(
                                new Server().url("https://" + domain)
                        )
                );
    }

    private OpenAPI defaultOpenAPIDefinition() {
        return new OpenAPI()
                .info(
                        new Info()
                                .contact(
                                        new Contact()
                                                .name("Marcin Startek")
                                                .email("icemajorr2@gmail.com")
                                )
                                .description("API documentation for Alkolicznik")
                                .title("Alkolicznik API")
                )
                .tags(List.of(
                                new Tag().name("Account"),
                                new Tag().name("Beer"),
                                new Tag().name("Store"),
                                new Tag().name("Beer Price"),
                                new Tag().name("Image"),
                                new Tag().name("City")
                        )
                )
                .schemaRequirement("JWT Authentication",
                        new SecurityScheme()
                                .name("JWT Authentication")
                                .description("Paste token below or use <b>/api/auth/authenticate</b> endpoint (which also sets a cookie)")
                                .scheme("bearer")
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                );
    }
}
