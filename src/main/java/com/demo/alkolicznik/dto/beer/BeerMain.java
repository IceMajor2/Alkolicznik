package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public abstract class BeerMain {

	//	protected String brand;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected String type;

	@Positive(message = "Volume must be a positive number")
	protected Double volume;

	abstract String getFullName();

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
