package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.*;

@Table(name = "store")
@Entity(name = "Store")
@JsonPropertyOrder({"id", "name", "city", "street"})
@NoArgsConstructor
@Getter
@Setter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String street;

    @JsonIgnore
    @OneToMany(mappedBy = "store",
            cascade = {CascadeType.ALL, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<BeerPrice> prices = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addBeer(Beer beer, double price) {
        addBeer(beer, Monetary.getDefaultAmountFactory()
                .setCurrency("PLN").setNumber(price).create());
    }

    public void addBeer(Beer beer, MonetaryAmount price) {
        BeerPrice beerPrice = new BeerPrice(this, beer, price);
        this.prices.add(beerPrice);
        beer.getPrices().add(beerPrice);
    }

    public BeerPrice removeBeer(Beer beer) {
        for (Iterator<BeerPrice> iterator = prices.iterator();
             iterator.hasNext(); ) {
            BeerPrice beerPrice = iterator.next();
            if (beerPrice.getStore().equals(this) &&
                    beerPrice.getBeer().equals(beer)) {
                BeerPrice copy = beerPrice.clone();
                iterator.remove();
                beerPrice.getBeer().getPrices().remove(beerPrice);
                beerPrice.setStore(null);
                beerPrice.setBeer(null);
                return copy;
            }
        }
        return null;
    }

    public Optional<BeerPrice> getBeer(String beerFullname) {
        for (BeerPrice beer : prices) {
            if (beer.getBeer().getFullName().equals(beerFullname)) {
                return Optional.of(beer);
            }
        }
        return Optional.empty();
    }

    public Optional<BeerPrice> getBeer(Long beerId) {
        for (BeerPrice beer : prices) {
            if (beer.getBeer().getId() == beerId) {
                return Optional.of(beer);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.name + ", " + this.city + " " + this.street + " (" + this.id + ")";
    }
}
