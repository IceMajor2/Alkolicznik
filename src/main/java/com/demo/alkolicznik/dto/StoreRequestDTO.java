package com.demo.alkolicznik.dto;

import jakarta.validation.constraints.NotBlank;

public class StoreRequestDTO {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
