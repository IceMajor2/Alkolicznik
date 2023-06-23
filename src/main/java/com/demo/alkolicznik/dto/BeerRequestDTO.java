package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BeerRequestDTO {

    @NotNull(message = "Beer's brand was not specified")
    @NotBlank(message = "Beer's brand must not be blank")
    private String brand;
    private String type;
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
        beer.setVolume(this.volume);
        return beer;
    }
}
