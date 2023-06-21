package com.demo.alkolicznik.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class BeerPriceKey implements Serializable {

    @Column(name = "beer_id")
    private Long beerId;

    @Column(name = "store_id")
    private Long storeId;

    public BeerPriceKey() {
    }

    public BeerPriceKey(Long beerId, Long storeId) {
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
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof BeerPriceKey)) {
            return false;
        }
        BeerPriceKey compareObj = (BeerPriceKey) obj;
        return compareObj.beerId == this.beerId &&
                compareObj.storeId == this.storeId;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}
