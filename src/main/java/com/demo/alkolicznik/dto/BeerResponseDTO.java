package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BeerResponseDTO {

    private Long id;
    @JsonProperty("name")
    private String fullName;
    private double volume;

    public BeerResponseDTO() {}

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
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
}
