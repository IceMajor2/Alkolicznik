package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ImageDeleteDTO {

	private BeerResponseDTO beer;
	private String message = "Image was deleted successfully!";
}
