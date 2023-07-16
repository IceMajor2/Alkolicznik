package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String street;

    @OneToMany(mappedBy = "store",
            cascade = {CascadeType.ALL, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<BeerPrice> prices = new HashSet<>();

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
