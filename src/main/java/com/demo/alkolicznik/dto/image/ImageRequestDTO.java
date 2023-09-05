package com.demo.alkolicznik.dto.image;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ImageRequestDTO {

	@NotBlank
	private String imagePath;
}
