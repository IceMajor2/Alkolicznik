package com.demo.alkolicznik.dto.security;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

	@NotBlank(message = "You did not specify a username")
	private String username;
	@NotBlank(message = "You did not specify a password")
	private String password;
}
