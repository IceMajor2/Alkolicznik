package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({"store_name", "beer_name", "url", "remote_id"})
public class ImageResponseDTO {

    //	@JsonProperty("url")
//	private String imageUrl;
//
//	@JsonProperty("remote_id")
//	private String remoteId;
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	@JsonProperty("store_name")
//	private String storeName;
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	@JsonProperty("beer_name")
//	private String beerName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StoreImage storeImage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BeerImage beerImage;

    //	public ImageResponseDTO(ImageModel image) {
//		this.imageUrl = image.getImageUrl();
//		this.remoteId = image.getRemoteId();
//		if (image instanceof BeerImage) {
//			this.beerName = ((BeerImage) image).getBeer().getFullName();
//		} else if (image instanceof StoreImage) {
//			this.storeName = ((StoreImage) image).getStoreName();
//		}
//	}
    public ImageResponseDTO(ImageModel imageModel) {
        if (imageModel instanceof BeerImage) {
            this.beerImage = ((BeerImage) imageModel);
        } else if (imageModel instanceof StoreImage) {
            this.storeImage = ((StoreImage) imageModel);
        }
    }
}
