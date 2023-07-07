package com.demo.alkolicznik.dto.requests;

import com.demo.alkolicznik.exceptions.annotations.NotBlankIfExists;
import com.demo.alkolicznik.models.Beer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerRequestDTO {

    @NotBlank(message = "Brand was not specified")
    private String brand;
    @NotBlankIfExists(message = "Type was not specified")
    private String type;
    @Positive(message = "Volume must be a positive number")
    private Double volume = 0.5;

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
