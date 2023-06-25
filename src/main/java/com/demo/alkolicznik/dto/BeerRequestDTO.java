package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Beer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BeerRequestDTO {

    @NotBlank(message = "Brand was not specified")
    private String brand;
    @NotBlankIfExists(message = "Type was not specified")
    private String type;
    @Positive(message = "Volume must be a positive number")
    private Double volume = 0.5;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Beer convertToModel() {
        Beer beer = new Beer();
        beer.setBrand(this.brand);
        if(this.type != null) {
            beer.setType(this.type);
        }
        if(this.volume == null) {
            beer.setVolume(0.5);
        } else {
            beer.setVolume(volume);
        }
        return beer;
    }
}
