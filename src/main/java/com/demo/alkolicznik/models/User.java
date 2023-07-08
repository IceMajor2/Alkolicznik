package com.demo.alkolicznik.models;

import com.demo.alkolicznik.dto.requests.UserRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotEmpty
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;
    private boolean accountNonLocked = true;

    public User(UserRequestDTO userRequestDTO) {
        this.username = userRequestDTO.getUsername();
        this.password = userRequestDTO.getPassword();
        this.roles = new HashSet<>(Set.of(Roles.USER));
    }
}
