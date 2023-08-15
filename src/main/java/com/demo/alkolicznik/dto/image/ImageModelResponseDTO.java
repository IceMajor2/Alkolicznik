package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({ "store_name", "beer_name", "url", "remote_id" })
// TODO: Rename this class to 'ImageResponseDTO'
public class ImageModelResponseDTO {

	@JsonProperty("url")
	private String imageUrl;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("store_name")
	private String storeName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("beer_name")
	private String beerName;

	public ImageModelResponseDTO(ImageModel image) {
		this.imageUrl = image.getImageUrl();
		this.remoteId = image.getRemoteId();
		if (image instanceof BeerImage) {
			this.beerName = ((BeerImage) image).getBeer().getFullName();
		} else if (image instanceof StoreImage) {
			this.storeName = ((StoreImage) image).getStoreName();
		}
	}
}
