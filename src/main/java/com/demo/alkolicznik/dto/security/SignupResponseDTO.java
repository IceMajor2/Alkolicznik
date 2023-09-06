package com.demo.alkolicznik.dto.security;

import com.demo.alkolicznik.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupResponseDTO {

    private Long id;
    private String username;
    private String role;
    private String token;

    public SignupResponseDTO(User user, String jwt) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.token = jwt;
    }
}
