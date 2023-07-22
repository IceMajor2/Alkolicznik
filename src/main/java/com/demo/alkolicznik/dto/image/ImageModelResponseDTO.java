package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.ImageModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonPropertyOrder({"url", "image_component"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ImageModelResponseDTO {

    @JsonProperty("url")
    private String imageUrl;

    @JsonProperty("external_id")
    private String externalId;

    public ImageModelResponseDTO(ImageModel image) {
        this.imageUrl = image.getImageUrl();
        this.externalId = image.getExternalId();
    }
}
