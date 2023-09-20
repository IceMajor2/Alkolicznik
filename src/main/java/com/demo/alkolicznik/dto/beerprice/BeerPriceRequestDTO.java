package com.demo.alkolicznik.dto.beerprice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BeerPriceRequestDTO {

    @JsonProperty("beer_name")
    @NotBlank(message = "Beer (its name and type) was not specified")
    private String beerName;

    @JsonProperty("beer_volume")
    @Positive(message = "Volume must be a positive number")
    private Double beerVolume = 0.5;

    @JsonProperty("beer_price")
    @NotNull(message = "Price was not specified")
    @Positive(message = "Price must be a positive number")
    private Double price;
}
