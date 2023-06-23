package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BeerResponseDTO {

    private Long id;
    @JsonProperty("name")
    private String fullName;
    private double volume;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "brand")
    private String brand;
    @JsonIgnore
    private String type;

    public BeerResponseDTO() {}

    public BeerResponseDTO(Beer beer) {
        this.id = beer.getId();
        this.fullName = beer.getFullName();
        this.volume = beer.getVolume();
        this.brand = beer.getBrand();
        this.type = beer.getType();
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
        StringBuilder sb = new StringBuilder("ID: ").append(this.id).append('\n');
        sb.append("Full name: ").append(this.getFullName()).append('\n');
        sb.append("Brand: ").append(this.brand).append('\n');
        sb.append("Type: ");
        if(this.type != null) {
            sb.append(this.type);
        } else {
            sb.append("---");
        }
        sb.append('\n').append("Volume: ").append(this.volume);
        return sb.toString();
    }
}
