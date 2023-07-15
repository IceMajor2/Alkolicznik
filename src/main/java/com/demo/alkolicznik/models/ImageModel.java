package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.html.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

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

    public Image getImageComponent() {
        if(imageComponent == null) {
            setImageComponent();
        }
        return imageComponent;
    }

    public void setImageComponent() {
        this.imageComponent = new Image(this.imageUrl, "No image");
    }
}
