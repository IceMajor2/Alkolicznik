package com.demo.alkolicznik.models;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "StoreImage")
@Table(name = "store_image")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StoreImage extends ImageModel {

	@Id
	@Column(name = "store_id")
	private Long storeId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "store_id")
	private Store store;

	public StoreImage(String imageUrl, String remoteId) {
		super(imageUrl, remoteId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof StoreImage)) {
			return false;
		}
		StoreImage that = (StoreImage) o;
		return Objects.equals(super.imageUrl, that.imageUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(imageUrl);
	}
}
