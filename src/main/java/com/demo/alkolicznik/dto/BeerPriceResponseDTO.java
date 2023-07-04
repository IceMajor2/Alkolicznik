package com.demo.alkolicznik.dto;

import com.demo.alkolicznik.models.BeerPrice;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"beer", "price", "store"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BeerPriceResponseDTO {

    private BeerResponseDTO beer;
    private StoreResponseDTO store;
    private String price;

    public BeerPriceResponseDTO(BeerPrice beerPrice) {
        this.store = new StoreResponseDTO(beerPrice.getStore());
        this.beer = new BeerResponseDTO(beerPrice.getBeer());
        this.price = beerPrice.getPrice().toString();
    }

    @Override
    public String toString() {
        return "%s: %s - %.2fzl".formatted(this.store.getName(), this.beer.getFullName(), this.price);
    }
}
