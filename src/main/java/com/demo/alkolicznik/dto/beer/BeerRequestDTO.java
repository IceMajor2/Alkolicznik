package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({ "brand", "type", "volume", "image_path" })
public class BeerRequestDTO {

	@NotBlank(message = "Brand was not specified")
	private String brand;

	private String type;

	@Positive(message = "Volume must be a positive number")
	private Double volume;

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

	public void setVolume(Double volume) {
		if(volume == null) {
			this.volume = 0.5;
			return;
		}
		this.volume = volume;
	}

//	public void setType(String type) {
//		if(type != null && type.isBlank()) {
//			this.type = null;
//			return;
//		}
//		this.type = type;
//	}
}
