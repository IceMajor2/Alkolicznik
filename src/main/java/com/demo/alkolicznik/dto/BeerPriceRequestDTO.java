package com.demo.alkolicznik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerPriceRequestDTO {

    @JsonProperty("beer_name")
    @NotBlank(message = "Beer was not specified")
    private String beerName;

    @JsonProperty("beer_volume")
    @Positive(message = "Volume must be a positive number")
    private Double beerVolume = 0.5;

    @JsonProperty("beer_price")
    @NotNull(message = "Price was not specified")
    @Positive(message = "Price must be a positive number")
    private Double price;
}
