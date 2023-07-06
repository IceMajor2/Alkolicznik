package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Objects;

@JsonPropertyOrder({"id", "brand", "type", "volume"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerResponseDTO {

    private Long id;
    private String brand;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;
    private double volume;

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
//        this.fullName = beer.getFullName();
        this.brand = beer.getBrand();
        this.type = beer.getType();
        this.volume = beer.getVolume();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerResponseDTO that = (BeerResponseDTO) o;
        return Double.compare(that.volume, volume) == 0 && Objects.equals(id, that.id) && Objects.equals(brand, that.brand) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, type, volume);
    }
}
