package com.demo.alkolicznik.dto.responses;

import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@JsonPropertyOrder({"username", "roles"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserResponseDTO {

    private String username;
    private Set<Roles> roles;

    public UserResponseDTO(User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles();
    }
}
