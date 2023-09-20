package com.demo.alkolicznik.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageRequestDTO {

	@NotBlank
	@JsonProperty("image_path")
	private String imagePath;
}
