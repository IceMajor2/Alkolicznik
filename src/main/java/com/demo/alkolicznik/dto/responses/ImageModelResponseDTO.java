package com.demo.alkolicznik.dto.responses;

import com.demo.alkolicznik.models.ImageModel;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import lombok.*;

@JsonPropertyOrder({"url", "image_component"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ImageModelResponseDTO {

    @Column(name = "url")
    private String imageUrl;

    @Column(name = "external_id")
    private String externalId;

    public ImageModelResponseDTO(ImageModel image) {
        this.imageUrl = image.getImageUrl();
        this.externalId = image.getExternalId();
    }
}
