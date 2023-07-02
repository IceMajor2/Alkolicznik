package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @JsonIgnore
    @OneToMany(mappedBy = "store",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    private Set<BeerPrice> prices = new HashSet<>();

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
        return "ID %d. %s, %s (%s)".formatted(this.id, this.name, this.street, this.city);
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

    public Optional<BeerPrice> getBeer(String beerFullname) {
        for (BeerPrice beer : prices) {
            if (beer.getBeer().getFullName().equals(beerFullname)) {
                return Optional.of(beer);
            }
        }
        return Optional.empty();
    }
}
