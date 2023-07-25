package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerRequestDTO {

	@NotBlank(message = "Brand was not specified")
	private String brand;

	private String type;

	@Positive(message = "Volume must be a positive number")
	private Double volume = 0.5;

	// TODO: Add regex
	private String imagePath;

	public Beer convertToModelNoImage() {
		Beer beer = new Beer();
		beer.setBrand(this.brand);
		if (this.type != null) {
			if (this.type.isBlank()) beer.setType(null);
			else beer.setType(this.type);
		}
		if (this.volume == null) {
			beer.setVolume(0.5);
		}
		else {
			beer.setVolume(volume);
		}
		return beer;
	}

	@JsonIgnore
	public String getFullName() {
		StringBuilder sb = new StringBuilder(this.brand);
		if (this.type != null) {
			sb.append(" ");
			sb.append(this.type);
		}
		return sb.toString();
	}

	public void setType(String type) {
		if (type != null && type.isBlank()) {
			this.type = null;
			return;
		}
		this.type = type;
	}

	public void setVolume(Double volume) {
		if (volume == null) {
			this.volume = 0.5;
			return;
		}
		this.volume = volume;
	}
}
