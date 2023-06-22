package com.demo.alkolicznik.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BeerPriceRequestDTO {

    @JsonProperty("name")
    private String fullname;
    private Double price;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
