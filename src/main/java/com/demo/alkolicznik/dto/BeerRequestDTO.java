package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;

public class BeerRequestDTO {

    @Nonnull
    private String brand;
    private String type;
    @Positive
    @Value("0.5")
    private Double volume = 0.5;

    @Nonnull
    public String getBrand() {
        return brand;
    }

    public void setBrand(@Nonnull String brand) {
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
        beer.setVolume(this.volume);
        return beer;
    }
}
