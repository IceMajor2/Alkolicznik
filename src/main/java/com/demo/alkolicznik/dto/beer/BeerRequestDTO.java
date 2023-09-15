package com.demo.alkolicznik.dto.beer;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonPropertyOrder({"brand", "type", "volume"})
public class BeerRequestDTO {

    @NotBlank(message = "Brand was not specified")
    private String brand;

    private String type;

    @Positive(message = "Volume must be a positive number")
    private Double volume;

    public BeerRequestDTO(BeerResponseDTO response) {
        this.brand = response.getBrand();
        this.type = response.getType();
        this.volume = response.getVolume();
    }

    public static Beer toModel(BeerRequestDTO request) {
        Beer beer = new Beer();
        beer.setBrand(request.getBrand());
        if (request.getType() != null && request.getType().isBlank()) {
            beer.setType(null);
        } else {
            beer.setType(request.getType());
        }
        if (request.getVolume() == null) {
            beer.setVolume(0.5);
        } else {
            beer.setVolume(request.getVolume());
        }
        return beer;
    }

    public static Beer toOverwrittenModel(BeerRequestDTO request, Beer toOverwrite) {
        Beer toOverwriteClone = (Beer) toOverwrite.clone();
        Beer partiallyOverwritten = toModel(request);
        toOverwriteClone.setBrand(partiallyOverwritten.getBrand());
        toOverwriteClone.setType(partiallyOverwritten.getType());
        toOverwriteClone.setVolume(partiallyOverwritten.getVolume());
        return toOverwriteClone;
    }

    @JsonIgnore
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ").append(this.type);
        }
        return sb.toString();
    }

    public void setVolume(Double volume) {
        if (volume == null) {
            this.volume = 0.5;
            return;
        }
        this.volume = volume;
    }
}
