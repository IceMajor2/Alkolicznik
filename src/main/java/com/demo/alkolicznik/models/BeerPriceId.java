package com.demo.alkolicznik.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class BeerPriceId implements Serializable {

    @Column(name = "beer_id")
    private Long beerId;

    @Column(name = "store_id")
    private Long storeId;

    public BeerPriceId(Long beerId, Long storeId) {
        this.beerId = beerId;
        this.storeId = storeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (beerId ^ (beerId >>> 32));
        result = prime * result + (int) (storeId ^ (storeId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BeerPriceId)) {
            return false;
        }
        BeerPriceId compareObj = (BeerPriceId) obj;
        return Objects.equals(this.beerId, compareObj.beerId) &&
                Objects.equals(this.storeId, compareObj.storeId);
    }
}
