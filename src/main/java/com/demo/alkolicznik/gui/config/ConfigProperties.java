package com.demo.alkolicznik.gui.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "gui")
@Data
@Validated
public class ConfigProperties {

    @NotBlank
    private String defaultCity;
    @NotBlank
    private String country;
}
