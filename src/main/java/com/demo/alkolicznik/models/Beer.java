package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "brand", "type", "volume"})
@NoArgsConstructor
@Getter
@Setter
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String type;
    private Double volume;

    @OneToMany(mappedBy = "beer",
            cascade = {CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
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

    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Beer beer = (Beer) o;
        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.getBrand() + " " + this.getType() + " (" + this.id + ") [" + this.volume + "l]";
    }
}
