package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.dto.UpdateModel;
import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerUpdateDTO implements UpdateModel<Beer> {

    @NotBlankIfExists(message = "Brand was not specified")
    private String brand;

    private String type;
    @Positive(message = "Volume must be a positive number")
    private Double volume;
    // TODO: Add regex
    private String imagePath;

    public BeerUpdateDTO(BeerRequestDTO requestDTO) {
        this.brand = requestDTO.getBrand();
        this.type = requestDTO.getType();
        this.volume = requestDTO.getVolume();
        this.imagePath = requestDTO.getImagePath();
    }

    @Override
    public boolean propertiesMissing() {
        return brand == null && type == null && volume == null && imagePath == null;
    }

    @Override
    public boolean anythingToUpdate(Beer beer) {
        // for now: not going to compare external image with the new, requested one
        if(this.imagePath != null) {
            return true;
        }
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

    public boolean imageToDelete() {
        return this.brand != null || this.type != null;
    }

    @JsonIgnore
    public String getFullName() {
        if (this.brand == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
    }

    public Beer convertToModelNoImage() {
        Beer beer = new Beer();
        if (this.brand == null) {
            return null;
        }
        beer.setBrand(this.brand);
        if (this.type != null) {
            beer.setType(this.type);
        }
        if (this.volume == null) {
            beer.setVolume(0.5);
            return beer;
        }
        beer.setVolume(this.volume);
        return beer;
    }
}