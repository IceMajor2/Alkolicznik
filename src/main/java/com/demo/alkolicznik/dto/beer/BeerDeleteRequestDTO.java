package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
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
}
