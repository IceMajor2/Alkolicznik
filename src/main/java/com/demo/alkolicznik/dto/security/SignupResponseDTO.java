package com.demo.alkolicznik.dto.security;

import lombok.Data;

@Data
public class SignupResponseDTO {

    private Long id;
    private String username;
    private String role;
    private String token;
}
