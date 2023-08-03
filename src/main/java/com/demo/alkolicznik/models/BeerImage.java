package com.demo.alkolicznik.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.html.Image;
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
import org.hibernate.validator.constraints.URL;

@Entity(name = "BeerImage")
@Table(name = "beer_image")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerImage {

    @Id
    @Column(name = "beer_id")
    private Long id;

	@Column(name = "remote_id")
    private String remoteId;

    @Column(name = "url")
    @URL
    private String imageUrl;

    @JsonIgnore
	@Column(name = "image_component")
    private Image imageComponent;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    private Beer beer;

    public BeerImage(String imageUrl, String remoteId) {
        this.imageUrl = imageUrl;
        this.remoteId = remoteId;
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
        return Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl);
    }
}
