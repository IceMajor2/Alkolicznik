package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerRequestDTO extends BeerMain {

	@NotBlank(message = "Brand was not specified")
	private String brand;

	@JsonProperty("image_path")
	private String imagePath;

	@JsonIgnore
	public String getFullName() {
		StringBuilder sb = new StringBuilder(this.brand);
		if (this.type != null) {
			sb.append(" ").append(this.type);
		}
		return sb.toString();
	}
}
