package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.BeerPrice;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonPropertyOrder({"beer", "price", "store"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BeerPriceResponseDTO {

    private BeerResponseDTO beer;
    private StoreResponseDTO store;
    private String price;

    public BeerPriceResponseDTO(BeerPrice beerPrice) {
        this.store = new StoreResponseDTO(beerPrice.getStore());
        this.beer = new BeerResponseDTO(beerPrice.getBeer());
        this.price = beerPrice.getPrice().toString();
    }
}
