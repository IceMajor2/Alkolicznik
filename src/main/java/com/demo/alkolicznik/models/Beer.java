package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.util.*;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "name"})
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private String name;

    @OneToMany(mappedBy = "beer",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    @JsonIgnore
    private Set<BeerPrice> prices = new HashSet<>();

    public Beer(String name) {
        this.name = name;
    }

    public Beer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public Set<BeerPrice> getPrices() {
        return prices;
    }

    public void setPrices(Set<BeerPrice> prices) {
        this.prices = prices;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Beer)) {
            return false;
        }

        Beer compare = (Beer) obj;

        return Objects.equals(this.name, compare.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "%s (ID: %d)".formatted(this.name, this.id);
    }
}
