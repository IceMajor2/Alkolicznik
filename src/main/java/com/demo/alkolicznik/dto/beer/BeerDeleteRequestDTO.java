package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "id", "name", "volume" })
@EqualsAndHashCode
public class BeerDeleteRequestDTO {

	@NotBlank(message = "Brand was not specified")
	private String brand;

	private String type;

	@Positive(message = "Volume must be a positive number")
	private Double volume = 0.5;

	public BeerDeleteRequestDTO(String brand, String type, Double volume) {
		this.brand = brand;
		setType(type);
		setVolume(volume);
	}

	@JsonIgnore
	public String getFullName() {
		if (this.brand == null) {
			return null;
		}
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
