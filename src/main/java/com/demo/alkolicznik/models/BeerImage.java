package com.demo.alkolicznik.models;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "BeerImage")
@Table(name = "beer_image")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BeerImage extends ImageModel {

    @Id
    @Column(name = "beer_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
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
