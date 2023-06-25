package com.demo.alkolicznik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeerPriceRequestDTO {

    @JsonProperty("beer_name")
    private String beerName;
    private Double price;

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
