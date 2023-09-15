package com.demo.alkolicznik.models;

import com.demo.alkolicznik.models.image.BeerImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "brand", "type", "volume"})
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Beer implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "beer_generator")
    @SequenceGenerator(name = "beer_generator", sequenceName = "beer_seq", allocationSize = 1)
    private Long id;

    private String brand;

    private String type;

    private Double volume;

    @OneToOne(mappedBy = "beer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private BeerImage image;

    @OneToMany(mappedBy = "beer",
            cascade = {CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BeerPrice> prices = new HashSet<>();

    public Beer(String brand) {
        this.brand = brand;
        this.volume = 0.5;
    }

    public Beer(String brand, String type) {
        this.brand = brand;
        this.type = type;
        this.volume = 0.5;
    }

    public Beer(String brand, String type, Double volume) {
        this.brand = brand;
        this.type = type;
        this.volume = volume;
    }

    public Beer(String brand, Double volume) {
        this.brand = brand;
        this.volume = volume;
    }

    public Optional<BeerImage> getImage() {
        return Optional.ofNullable(image);
    }

    /**
     * Removes all entities from 'beer_price' table that are associated with
     * this beer's id. The stores are also made to drop all references to this beer object.
     */
    public boolean deleteAllPrices() {
        for (Iterator<BeerPrice> iterator = prices.iterator();
             iterator.hasNext(); ) {
            BeerPrice beerPrice = iterator.next();
            iterator.remove();
            beerPrice.getStore().getPrices().remove(beerPrice);
            beerPrice.setStore(null);
            beerPrice.setBeer(null);
        }
        return true;
    }

    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Beer)) {
            return false;
        }
        Beer beer = (Beer) o;
        return Objects.equals(brand, beer.getBrand())
                && Objects.equals(type, beer.getType())
                && Objects.equals(volume, beer.getVolume())
                && Objects.equals(image, beer.getImage().orElse(null));
    }

    @Override
    public Object clone() {
        try {
            return (Beer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, type, volume, image);
    }
}
