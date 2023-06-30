package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.BeerPrice;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"store_id", "store_name", "beer_id", "beer_name", "price"})
@NoArgsConstructor
@Getter
@Setter
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

    public BeerPriceResponseDTO(BeerPrice beerPrice) {
        this.storeId = beerPrice.getId().getStoreId();
        this.storeName = beerPrice.getStore().getName();
        this.beerId = beerPrice.getId().getBeerId();
        this.beerName = beerPrice.getBeer().getFullName();
        this.price = beerPrice.getPrice();
    }
}
