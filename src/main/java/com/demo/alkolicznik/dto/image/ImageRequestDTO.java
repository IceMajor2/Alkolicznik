package com.demo.alkolicznik.dto.image;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImageRequestDTO {

	@NotBlank
	private String imagePath;
}
