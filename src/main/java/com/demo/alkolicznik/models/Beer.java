package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "Beer")
@Table(name = "beer")
@JsonPropertyOrder({"id", "brand", "type", "volume"})
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String brand;
    private String type;
    private Double volume;
    @OneToOne(mappedBy = "beer",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Image image;

    @OneToMany(mappedBy = "beer",
            cascade = {CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
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

    @JsonProperty("name")
    public String getFullName() {
        StringBuilder sb = new StringBuilder(this.brand);
        if (this.type != null) {
            sb.append(" ");
            sb.append(this.type);
        }
        return sb.toString();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || !(o instanceof Beer)) return false;
//        Beer beer = (Beer) o;
//        System.out.println(this);
//        System.out.println(beer);
//        System.out.println(brand + " | " + beer.brand + " ->> "+Objects.equals(brand, beer.brand));
//        System.out.println(Objects.equals(type, beer.type));
//        System.out.println(Objects.equals(volume, beer.volume));
//        return Objects.equals(brand, beer.brand) && Objects.equals(type, beer.type) && Objects.equals(volume, beer.volume);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(brand, type, volume);
//    }

    @Override
    public String toString() {
        return this.getBrand() + " " + this.getType() + " (" + this.id + ") [" + this.volume + "l]";
    }
}
