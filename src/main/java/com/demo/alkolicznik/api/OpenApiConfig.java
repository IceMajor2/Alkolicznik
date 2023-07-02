package com.demo.alkolicznik.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Marcin Startek",
                        email = "icemajorr2@gmail.com"
                ),
                description = "API documentation for Alkolicznik",
                title = "Alkolicznik Web API",
                version = "1.0 alpha"
        )
)
@SecurityScheme(
        name = "Basic Authentication",
        scheme = "basic",
        type = SecuritySchemeType.HTTP
)
public class OpenApiConfig {
}
