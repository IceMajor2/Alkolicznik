package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.image.BeerImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonPropertyOrder({"beer_name", "remote_id", "url"})
@Data
public class BeerImageResponseDTO {

    @JsonProperty("beer_name")
    private String beerFullName;
    @JsonProperty("remote_id")
    private String remoteId;
    @JsonProperty("url")
    private String url;

    public BeerImageResponseDTO(BeerImage beerImage) {
        this.beerFullName = beerImage.getBeer().getFullName();
        this.remoteId = beerImage.getRemoteId();
        this.url = beerImage.getImageUrl();
    }

    public static List<BeerImageResponseDTO> asList(Collection<BeerImage> beerImages) {
        return beerImages.stream()
                .map(BeerImageResponseDTO::new)
                .toList();
    }
}
