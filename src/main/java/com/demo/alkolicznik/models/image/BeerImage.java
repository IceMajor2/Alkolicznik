package com.demo.alkolicznik.models.image;

import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity(name = "BeerImage")
@Table(name = "beer_image")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class BeerImage extends ImageModel {

    @Id
    @Column(name = "beer_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    @JsonIgnore
    @ToString.Exclude
    private Beer beer;

    public BeerImage(String imageUrl, String remoteId) {
        super(imageUrl, remoteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof BeerImage)) {
            return false;
        }
        BeerImage that = (BeerImage) o;
        return Objects.equals(super.imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl);
    }
}
