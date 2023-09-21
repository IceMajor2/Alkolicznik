package com.demo.alkolicznik.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Marcin Startek",
                        email = "icemajorr2@gmail.com"
                ),
                description = "API documentation for Alkolicznik",
                title = "Alkolicznik API",
                version = "1.0"
        ),
        tags = {
                @Tag(name = "Account"),
                @Tag(name = "Beer"),
                @Tag(name = "Store"),
                @Tag(name = "Beer Price"),
                @Tag(name = "Image"),
                @Tag(name = "City")
        }
)
@SecurityScheme(
        name = "JWT Authentication",
        description = "Paste token below or use <b>/api/auth/authenticate</b> endpoint (which also sets a cookie)",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
