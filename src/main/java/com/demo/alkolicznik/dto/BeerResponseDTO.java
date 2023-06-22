package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder
public class BeerResponseDTO {

    private Long id;
    private String name;
    private double volume;

    public BeerResponseDTO() {}

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getFullname();
        this.volume = beer.getVolume();
    }

    public BeerResponseDTO(Long id, String name, double volume) {
        this.id = id;
        this.name = name;
        this.volume = volume;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return Double.compare(that.volume, volume) == 0 && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, volume);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.name);
        sb.append(" (ID: ");
        sb.append(this.id);
        sb.append(")");
        return sb.toString();
    }
}
