package com.demo.alkolicznik.dto.responses;

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

    public Double getAmountOnly() {
        int firstDigitIndex = findFirstDigitIndex(this.price);
        int lastDigitIndex = findLastDigitIndex(this.price, firstDigitIndex);

        return Double.valueOf(this.price.substring(firstDigitIndex, lastDigitIndex + 1));
    }

    private boolean isDigit(char character) {
        return character >= 48 && character <= 57;
    }

    private int findFirstDigitIndex(String string) {
        int currIndex = 0;
        for (char ch : string.toCharArray()) {
            if (isDigit(ch)) {
                return currIndex;
            }
            currIndex++;
        }
        return -1;
    }

    private int findLastDigitIndex(String string, int firstDigitIndex) {
        int currIndex = firstDigitIndex + 1;
        for (int i = currIndex; i < string.length(); i++) {
            if (string.charAt(i) == '.') {
                continue;
            }
            if (!isDigit(string.charAt(i))) {
                return i - 1;
            }
        }
        if (isDigit(string.charAt(string.length() - 1))) {
            return string.length() - 1;
        }
        return -1;
    }
}
