package com.demo.alkolicznik.dto.image;

import com.demo.alkolicznik.models.image.ImageModel;
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
@JsonPropertyOrder({ "url", "remote_id" })
public class ImageModelResponseDTO {

	@JsonProperty("url")
	private String imageUrl;

	@JsonProperty("remote_id")
	private String remoteId;

	public ImageModelResponseDTO(ImageModel image) {
		this.imageUrl = image.getImageUrl();
		this.remoteId = image.getRemoteId();
	}
}
