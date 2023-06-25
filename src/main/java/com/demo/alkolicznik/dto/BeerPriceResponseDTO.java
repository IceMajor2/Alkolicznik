package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.BeerPrice;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"store_id", "store_name", "beer_id", "beer_name", "price"})
public class BeerPriceResponseDTO {

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("beer_id")
    private Long beerId;

    @JsonProperty("beer_name")
    private String beerName;

    @JsonProperty("price")
    private Double price;

    public BeerPriceResponseDTO() {}

    public BeerPriceResponseDTO(BeerPrice beerPrice) {
        this.storeId = beerPrice.getId().getStoreId();
        this.storeName = beerPrice.getStore().getName();
        this.beerId = beerPrice.getId().getBeerId();
        this.beerName = beerPrice.getBeer().getFullName();
        this.price = beerPrice.getPrice();
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
