package com.demo.alkolicznik.dto.delete;

import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"beer", "price", "store", "status"})
public class BeerPriceDeleteDTO {

    private StoreResponseDTO store;
    private BeerResponseDTO beer;
    private String price;
    private String status;
}
