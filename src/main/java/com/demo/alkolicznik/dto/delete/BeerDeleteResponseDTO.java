package com.demo.alkolicznik.dto.delete;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeerDeleteResponseDTO {

    private Long id;
    @JsonProperty("name")
    private String fullName;
    private double volume;
    private String status = "Beer was deleted successfully!";

    public BeerDeleteResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
    }

    public BeerDeleteResponseDTO(Beer beer, String status) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
        this.status = status;
    }
}
