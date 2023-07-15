package com.demo.alkolicznik.dto.responses;

import com.demo.alkolicznik.models.Image;
import jakarta.persistence.Column;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ImageResponseDTO {

    @Column(name = "url")
    private String imageUrl;

    public ImageResponseDTO(Image image) {
        this.imageUrl = image.getImageUrl();
    }
}
