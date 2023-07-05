package com.demo.alkolicznik.dto.put;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerPriceUpdateDTO implements UpdateModel {

    @Positive(message = "Price must be a positive number")
    private Double price;

    @Override
    public boolean propertiesMissing() {
        return price == null;
    }
}
