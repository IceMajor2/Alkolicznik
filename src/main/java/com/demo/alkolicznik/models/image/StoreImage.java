package com.demo.alkolicznik.models.image;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.demo.alkolicznik.models.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "store_name")
	private String storeName;

	@OneToMany(mappedBy = "image", orphanRemoval = true)
	private Set<Store> stores = new HashSet<>();

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
