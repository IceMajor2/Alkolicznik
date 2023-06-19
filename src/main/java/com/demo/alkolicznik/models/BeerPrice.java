package com.demo.alkolicznik.models;

import jakarta.persistence.*;

@Entity
public class BeerPrice {

    @EmbeddedId
    BeerPriceKey id;

    @ManyToOne
    @MapsId("storeId")
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @MapsId("beerId")
    @JoinColumn(name = "beer_id")
    private Beer beer;

    private double price;

    public BeerPriceKey getId() {
        return id;
    }

    public void setId(BeerPriceKey id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
