package com.demo.alkolicznik.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Image")
@Table(name = "image")
@NoArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @Column(name = "beer_id")
    private Long id;
    private String imageAddress;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    private Beer beer;
}
