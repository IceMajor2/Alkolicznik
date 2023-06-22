package com.demo.alkolicznik.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

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
public class OpenApiConfig {
}
