package com.demo.alkolicznik.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "store")
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private String name;

    @OneToMany(mappedBy = "store")
    private Set<BeerPrice> prices;

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
}
