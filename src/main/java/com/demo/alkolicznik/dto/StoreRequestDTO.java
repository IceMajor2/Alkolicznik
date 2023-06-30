package com.demo.alkolicznik.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class StoreRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String street;
}
