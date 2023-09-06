package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id", "name", "volume", "image", "status"})
@EqualsAndHashCode
@ToString
public class BeerDeleteResponseDTO {

    private Long id;

    @JsonProperty("name")
    private String fullName;

    private Double volume;

    private String status = "Beer was deleted successfully!";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BeerImageResponseDTO image;

    public BeerDeleteResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
        beer.getImage().ifPresent(beerImage -> this.image = new BeerImageResponseDTO(beerImage));
    }

    public BeerDeleteResponseDTO(Beer beer, String status) {
        this(beer);
        this.status = status;
    }
}
