package com.demo.alkolicznik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeerPriceRequestDTO_ID {

    @JsonProperty("beer_id")
    @NotNull(message = "ID was not specified")
    private Long beerId;

    @JsonProperty("price")
    @NotNull(message = "Price was not specified")
    @Positive(message = "Price must be a positive number")
    private Double price;
}
