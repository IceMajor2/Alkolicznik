package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

	private String brand;

	private String type;

	private Double volume;

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
