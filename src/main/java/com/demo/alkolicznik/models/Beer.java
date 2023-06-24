package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "name", "volume"})
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String type;
    private Double volume;

    @OneToMany(mappedBy = "beer",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    @JsonIgnore
    private Set<BeerPrice> prices = new HashSet<>();

    public Beer(String brand) {
        this.brand = brand;
        this.volume = 0.5;
    }

    public Beer(String brand, String type) {
        this.brand = brand;
        this.type = type;
        this.volume = 0.5;
    }

    public Beer(String brand, String type, Double volume) {
        this.brand = brand;
        this.type = type;
        this.volume = volume;
    }

    public Beer(String brand, Double volume) {
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

    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if(this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
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
