package com.demo.alkolicznik.models;

import com.demo.alkolicznik.dto.requests.UserRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotEmpty
    @JsonProperty("roles")
    private Set<Roles> roles;
    @JsonIgnore
    private boolean accountNonLocked = true;

    public User(UserRequestDTO userRequestDTO) {
        this.username = userRequestDTO.getUsername();
        this.password = userRequestDTO.getPassword();
        this.roles = new HashSet<>(Set.of(Roles.USER));
    }
}
