package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "name"})
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nonnull
    private String brand;
    private String type;
    @Positive
    private Double volume;

    @OneToMany(mappedBy = "beer",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    @JsonIgnore
    private Set<BeerPrice> prices = new HashSet<>();

    public Beer(@Nonnull String brand) {
        this.brand = brand;
        this.volume = 0.5;
    }

    public Beer(@Nonnull String brand, String type) {
        this.brand = brand;
        this.type = type;
        this.volume = 0.5;
    }

    public Beer(@Nonnull String brand, String type, @Positive Double volume) {
        this.brand = brand;
        this.type = type;
        this.volume = volume;
    }

    public Beer(@Nonnull String brand, @Positive Double volume) {
        this.brand = brand;
        this.volume = volume;
    }

    public Beer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<BeerPrice> getPrices() {
        return prices;
    }

    public void setPrices(Set<BeerPrice> prices) {
        this.prices = prices;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(brand, beer.brand) && Objects.equals(type, beer.type) && Objects.equals(volume, beer.volume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type, volume);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(this.brand);
        if(!this.type.isEmpty()) {
            sb.append(" ");
            sb.append(this.type);
        }
        sb.append(" (ID: ");
        sb.append(this.id);
        sb.append(")");
        return sb.toString();
    }
}
