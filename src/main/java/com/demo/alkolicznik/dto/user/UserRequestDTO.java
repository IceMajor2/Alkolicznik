package com.demo.alkolicznik.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 12, message = "Password length must be at least 12 characters long")
    private String password;
}
