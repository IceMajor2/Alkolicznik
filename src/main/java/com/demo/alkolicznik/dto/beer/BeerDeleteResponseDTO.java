package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({ "id", "name", "volume", "image", "status" })
@EqualsAndHashCode
@ToString
public class BeerDeleteResponseDTO extends BeerMain {

	private Long id;

	@JsonProperty("name")
	private String fullName;

//	private Double volume;

	private String status = "Beer was deleted successfully!";

	private ImageModelResponseDTO image;

	public BeerDeleteResponseDTO(Beer beer) {
		this.id = beer.getId();
		this.fullName = beer.getFullName();
		super.volume = beer.getVolume();
		if (beer.getImage().isPresent()) {
			this.image = new ImageModelResponseDTO(beer.getImage().get());
		}
	}

	public BeerDeleteResponseDTO(Beer beer, String status) {
		this.id = beer.getId();
		this.fullName = beer.getFullName();
		super.volume = beer.getVolume();
		this.status = status;
		if (beer.getImage().isPresent()) {
			this.image = new ImageModelResponseDTO(beer.getImage().get());
		}
	}
}