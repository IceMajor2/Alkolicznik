package com.demo.alkolicznik.dto.beer;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	private String imagePath;

//	public Beer convertToModelNoImage() {
//		Beer beer = new Beer();
//		beer.setBrand(this.brand);
//		beer.setType(super.type);
//		beer.setVolume(super.volume);
//		return beer;
//	}

	@JsonIgnore
	public String getFullName() {
		StringBuilder sb = new StringBuilder(this.brand);
		if (this.type != null) {
			sb.append(" ").append(this.type);
		}
		return sb.toString();
	}
}
