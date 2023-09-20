package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({ "id", "brand", "type", "volume", "image" })
public class BeerResponseDTO {

	private Long id;

	private String brand;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String type;

	private Double volume;

	@ToString.Exclude
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BeerImageResponseDTO image;

	public BeerResponseDTO(Beer beer) {
		this.id = beer.getId();
		this.brand = beer.getBrand();
		this.type = beer.getType();
		this.volume = beer.getVolume();
		beer.getImage().ifPresent(beerImage -> this.image = new BeerImageResponseDTO(beerImage));
	}

	public static List<BeerResponseDTO> asList(Collection<Beer> beers) {
		return beers.stream()
				.map(BeerResponseDTO::new)
				.toList();
	}

	@JsonIgnore
	public String getFullName() {
		StringBuilder sb = new StringBuilder(this.brand);
		if (this.type != null) {
			sb.append(" ").append(this.type);
		}
		return sb.toString();
	}
}
