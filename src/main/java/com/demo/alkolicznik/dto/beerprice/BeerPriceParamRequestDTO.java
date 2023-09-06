package com.demo.alkolicznik.dto.beerprice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BeerPriceParamRequestDTO {

    @NotNull(message = "Store id was not specified")
    private Double storeId;
    @NotNull(message = "Beer id was not specified")
    private Double beerId;
    @NotNull(message = "Price was not specified")
    @Positive(message = "Price must be a positive number")
    private Double price;

    public BeerPriceParamRequestDTO(BeerPriceResponseDTO priceResponse) {
        this.storeId = priceResponse.getStore().getId().doubleValue();
        this.beerId = priceResponse.getBeer().getId().doubleValue();
        this.price = priceResponse.getAmountOnly();
    }

    public Long getLongStoreId() {
		return this.storeId.longValue();
	}

	public Long getLongBeerId() {
		return this.beerId.longValue();
	}
}
