package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity(name = "BeerPrice")
@Table(name = "beer_price")
@NoArgsConstructor
@Getter
@Setter
public class BeerPrice {

    @EmbeddedId
    private BeerPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storeId")
    @JsonIgnore
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("beerId")
    @JsonIgnore
    private Beer beer;

    @Column(name = "price")
    private double price;

    public BeerPrice(Store store, Beer beer, double price) {
        this.store = store;
        this.beer = beer;
        this.price = price;
        this.id = new BeerPriceId(beer.getId(), store.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerPrice beerPrice = (BeerPrice) o;
        return Objects.equals(store, beerPrice.store)
                && Objects.equals(beer, beerPrice.beer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, beer);
    }

    @Override
    public String toString() {
        return "%s: %s - %.2fzl".formatted(this.store.getName(), this.beer.getBrand(), this.price);
    }
}
