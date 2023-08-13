package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@JsonPropertyOrder({ "beer", "store_name", "message" })
public class ImageDeleteDTO {

	@JsonInclude(Include.NON_NULL)
	private BeerResponseDTO beer;

	@JsonInclude(Include.NON_NULL)
	private String storeName;

	private String message = "Image was deleted successfully!";

	public ImageDeleteDTO(Beer beer) {
		this.beer = new BeerResponseDTO(beer);
	}

	public ImageDeleteDTO(StoreImage image) {
		this.storeName = image.getStoreName();
	}
}
