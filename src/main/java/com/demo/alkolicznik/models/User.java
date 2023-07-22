package com.demo.alkolicznik.models;

import com.demo.alkolicznik.dto.user.UserRequestDTO;
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
@SequenceGenerator(
        name = "userIdSeq",
        sequenceName = "user_id_seq",
        allocationSize = 1
)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIdSeq")
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
        this.roles = new HashSet<>();
    }
}
