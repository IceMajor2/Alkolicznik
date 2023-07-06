package com.demo.alkolicznik.dto.delete;

import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.demo.alkolicznik.models.BeerPrice;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"beer", "price", "store", "status"})
public class BeerPriceDeleteDTO {

    private StoreResponseDTO store;
    private BeerResponseDTO beer;
    private String price;
    private String status = "Beer price was deleted successfully!";

    public BeerPriceDeleteDTO(BeerPrice beerPrice) {
        this.store = new StoreResponseDTO(beerPrice.getStore());
        this.beer = new BeerResponseDTO(beerPrice.getBeer());
        this.price = beerPrice.getPrice().getNumber() + " " + beerPrice.getPrice().getCurrency().getCurrencyCode();
    }
}
