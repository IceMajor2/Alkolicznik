package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CompositeType;

import javax.money.MonetaryAmount;
import java.util.Objects;

@Entity(name = "BeerPrice")
@Table(name = "beer_price")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BeerPrice {

    @EmbeddedId
    private BeerPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storeId")
    @JsonIgnore
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("beerId")
    @JsonIgnore
    private Beer beer;

    @AttributeOverride(
            name = "amount",
            column = @Column(name = "price_amount")
    )
    @AttributeOverride(
            name = "currency",
            column = @Column(name = "price_currency")
    )
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount price;

    public BeerPrice(Store store, Beer beer, MonetaryAmount price) {
        this.store = store;
        this.beer = beer;
        this.price = price;
        this.id = new BeerPriceId(beer.getId(), store.getId());
    }

    @JsonIgnore
    public Double getAmountOnly() {
        return price.getNumber().doubleValueExact();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerPrice beerPrice = (BeerPrice) o;
        return Objects.equals(store, beerPrice.store)
                && Objects.equals(beer, beerPrice.beer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, beer);
    }

    @Override
    public BeerPrice clone() {
        return new BeerPrice(this.id, this.store, this.beer, this.price);
    }
}
