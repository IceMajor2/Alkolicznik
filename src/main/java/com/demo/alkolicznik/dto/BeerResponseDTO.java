package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@JsonPropertyOrder({"id", "name", "volume"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeerResponseDTO {

    private Long id;
    @JsonProperty("name")
    private String fullName;
    private double volume;

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerResponseDTO that = (BeerResponseDTO) o;
        return Double.compare(that.volume, volume) == 0
                && Objects.equals(id, that.id)
                && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, volume);
    }

    @Override
    public String toString() {
        return "%s | %.2fl | ID: %d".formatted(this.fullName, this.volume, this.id);
    }
}
