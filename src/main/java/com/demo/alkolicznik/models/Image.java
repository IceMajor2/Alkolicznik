package com.demo.alkolicznik.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

@Entity(name = "Image")
@Table(name = "image")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Image {

    @Id
    @Column(name = "beer_id")
    private Long id;

    @Column(name = "url")
    @URL
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    private Beer beer;
}
