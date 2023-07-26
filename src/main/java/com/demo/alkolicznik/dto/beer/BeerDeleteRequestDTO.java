package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "id", "brand", "type", "volume" })
@EqualsAndHashCode
public class BeerDeleteRequestDTO extends BeerMain {

	@NotBlank(message = "Brand was not specified")
	private String brand;

//	private String type;
//
//	@Positive(message = "Volume must be a positive number")
//	private Double volume = 0.5;

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
			sb.append(" ").append(this.type);
		}
		return sb.toString();
	}
}
