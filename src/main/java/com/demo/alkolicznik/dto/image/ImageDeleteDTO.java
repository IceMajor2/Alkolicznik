package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ImageDeleteDTO {

	private BeerResponseDTO beer;
	private String message = "Image was deleted successfully!";

	public ImageDeleteDTO(Beer beer) {
		this.beer = new BeerResponseDTO(beer);
	}
}
