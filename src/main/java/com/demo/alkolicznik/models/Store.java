package com.demo.alkolicznik.models;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Table(name = "store")
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "store")
    private Set<BeerPrice> prices;

    public Store() {}

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
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof Store)) {
            return false;
        }

        Store compare = (Store) obj;

        return compare.getId() == this.getId() && compare.getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "%s (ID: %d)".formatted(this.name, this.id);
    }
}
