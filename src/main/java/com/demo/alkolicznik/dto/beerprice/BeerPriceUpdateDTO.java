package com.demo.alkolicznik.dto.beerprice;

import com.demo.alkolicznik.dto.UpdateModel;
import com.demo.alkolicznik.models.BeerPrice;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Deprecated(forRemoval = true)
public class BeerPriceUpdateDTO implements UpdateModel<BeerPrice> {

    @Positive(message = "Price must be a positive number")
    private Double price;

    @Override
    public boolean propertiesMissing() {
        return price == null;
    }

    @Override
    public boolean anythingToUpdate(BeerPrice beerPrice) {
        Double currPrice = beerPrice.getPrice().getNumber().doubleValueExact();
        Double upPrice = this.getPrice();
        return !currPrice.equals(upPrice);
    }
}
