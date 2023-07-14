package com.demo.alkolicznik.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerPriceParamRequestDTO {

    @NotNull(message = "Store id was not specified")
    private Double storeId;
    @NotNull(message = "Beer id was not specified")
    private Double beerId;
    @NotNull(message = "Price was not specified")
    @Positive(message = "Price must be a positive number")
    private Double price;
}
