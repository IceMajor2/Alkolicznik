package com.demo.alkolicznik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerPriceRequestDTO {

    @JsonProperty("beer_name")
    private String beerName;
    private Double price;
}
