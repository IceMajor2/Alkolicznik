package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.html.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Entity(name = "ImageModel")
@Table(name = "image")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImageModel {

    @Id
    @Column(name = "beer_id")
    private Long id;

    @Column(name = "url")
    @URL
    private String imageUrl;

    @JsonIgnore
    private Image imageComponent;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    private Beer beer;

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ImageModel)) {
            return false;
        }
        ImageModel that = (ImageModel) o;
        return Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl);
    }
}
