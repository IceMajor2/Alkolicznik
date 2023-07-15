package com.demo.alkolicznik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "beer_id")
    @JsonIgnore
    private Beer beer;
}
