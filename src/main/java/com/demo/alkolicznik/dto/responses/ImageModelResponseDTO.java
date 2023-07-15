package com.demo.alkolicznik.dto.responses;

import com.demo.alkolicznik.models.ImageModel;
import com.vaadin.flow.component.html.Image;
import jakarta.persistence.Column;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ImageModelResponseDTO {

    @Column(name = "url")
    private String imageUrl;

    private Image imageComponent;

    public ImageModelResponseDTO(ImageModel image) {
        this.imageUrl = image.getImageUrl();
        this.imageComponent = image.getImageComponent();
    }
}
