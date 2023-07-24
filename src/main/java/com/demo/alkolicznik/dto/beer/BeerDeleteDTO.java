package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id", "name", "volume", "status"})
@EqualsAndHashCode
public class BeerDeleteDTO {

    private Long id;
    @JsonProperty("name")
    private String fullName;
    private Double volume;
    private String status = "Beer was deleted successfully!";
	// TODO: Add image

    public BeerDeleteDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
    }

    public BeerDeleteDTO(Beer beer, String status) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
        this.status = status;
    }
}
