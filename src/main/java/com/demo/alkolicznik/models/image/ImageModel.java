package com.demo.alkolicznik.models.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.html.Image;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class ImageModel {

	@Column(name = "remote_id")
	protected String remoteId;

	@Column(name = "url")
	@URL
	protected String imageUrl;

	@JsonIgnore
	@Column(name = "image_component")
	protected Image imageComponent;

	public ImageModel(String imageUrl, String remoteId) {
		this.imageUrl = imageUrl;
		this.remoteId = remoteId;
	}
}
