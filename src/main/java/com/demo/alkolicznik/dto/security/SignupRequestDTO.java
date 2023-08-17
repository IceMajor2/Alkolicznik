package com.demo.alkolicznik.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {

    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 12, message = "Password length must be at least 12 characters long")
    private String password;
}
