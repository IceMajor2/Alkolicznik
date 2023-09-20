package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonPropertyOrder({"store_name", "remote_id", "url"})
@Data
public class StoreImageResponseDTO {

    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("remote_id")
    private String remoteId;
    @JsonProperty("url")
    private String url;

    public StoreImageResponseDTO(StoreImage storeImage) {
        this.storeName = storeImage.getStoreName();
        this.remoteId = storeImage.getRemoteId();
        this.url = storeImage.getImageUrl();
    }

    public static List<StoreImageResponseDTO> asList(Collection<StoreImage> storeImages) {
        return storeImages.stream()
                .map(StoreImageResponseDTO::new)
                .toList();
    }
}
