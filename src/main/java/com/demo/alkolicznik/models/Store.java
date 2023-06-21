package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Table(name = "store")
@Entity(name = "Store")
@JsonPropertyOrder({"id", "name"})
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "store",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    @JsonIgnore
    private Set<BeerPrice> prices = new HashSet<>();

    public Store() {
    }

    public Store(String name) {
        this.name = name;
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
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Store)) {
            return false;
        }

        Store compare = (Store) obj;

        return compare.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "%s (ID: %d)".formatted(this.name, this.id);
    }

    public void addBeer(Beer beer, double price) {
        BeerPrice beerPrice = new BeerPrice(this, beer, price);
        this.prices.add(beerPrice);
        beer.getPrices().add(beerPrice);
    }

    public void removeBeer(Beer beer) {
        for (Iterator<BeerPrice> iterator = prices.iterator();
             iterator.hasNext(); ) {
            BeerPrice beerPrice = iterator.next();

            if (beerPrice.getStore().equals(this) &&
                    beerPrice.getBeer().equals(beer)) {
                iterator.remove();
                beerPrice.getBeer().getPrices().remove(beerPrice);
                beerPrice.setStore(null);
                beerPrice.setBeer(null);
            }
        }
    }

    public Optional<BeerPrice> getBeer(String beerName) {
        for (BeerPrice beer : prices) {
            if (beer.getBeer().getName().equals(beerName)) {
                return Optional.of(beer);
            }
        }
        return Optional.empty();
    }
}
