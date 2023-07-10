package com.demo.alkolicznik.dto.put;

import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Beer;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class BeerUpdateDTO implements UpdateModel<Beer> {

    @NotBlankIfExists(message = "Brand was not specified")
    private String brand;

    private String type;
    @Positive(message = "Volume must be a positive number")
    private Double volume;

    @Override
    public boolean propertiesMissing() {
        return brand == null && type == null && volume == null;
    }

    @Override
    public boolean anythingToUpdate(Beer beer) {
        String currBrand = beer.getBrand();
        String currType = beer.getType();
        Double currVolume = beer.getVolume();

        String upBrand = this.getBrand();
        String upType = this.getType();
        Double upVolume = this.getVolume();

        if (upBrand != null && !Objects.equals(currBrand, upBrand)) {
            return true;
        }
        if (upType != null && !Objects.equals(currType, upType)) {
            return true;
        }
        if (upVolume != null && !Objects.equals(currVolume, upVolume)) {
            return true;
        }
        return false;
    }
}
