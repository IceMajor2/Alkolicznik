package com.demo.alkolicznik.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "store")
@Entity(name = "Store")
@JsonPropertyOrder({ "id", "name", "city", "street", "image" })
@NoArgsConstructor
@Getter
@Setter
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String city;

	private String street;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "name",
			referencedColumnName = "store_name",
			insertable = false,
			updatable = false
	)
	private StoreImage image;

	@OneToMany(mappedBy = "store",
			cascade = { CascadeType.ALL, CascadeType.MERGE },
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<BeerPrice> prices = new HashSet<>();

	@Override
	public String toString() {
		return this.name + ", " + this.city + " " + this.street + " (" + this.id + ")";
	}

	public Optional<StoreImage> getImage() {
		return Optional.ofNullable(image);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, city, street);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof Store)) {
			return false;
		}
		Store store = (Store) o;
		return Objects.equals(name, store.getName()) &&
				Objects.equals(city, store.getCity()) &&
				Objects.equals(street, store.getStreet());
	}

	public void saveBeer(Beer beer, double price) {
		saveBeer(beer, Monetary.getDefaultAmountFactory()
				.setCurrency("PLN").setNumber(price).create());
	}

	public void saveBeer(Beer beer, MonetaryAmount price) {
		BeerPrice beerPrice = new BeerPrice(this, beer, price);
		this.prices.add(beerPrice);
		beer.getPrices().add(beerPrice);
	}

	public BeerPrice deleteBeer(Beer beer) {
		for (Iterator<BeerPrice> iterator = prices.iterator();
			 iterator.hasNext(); ) {
			BeerPrice beerPrice = iterator.next();

			if (beerPrice.getStore().equals(this) &&
					beerPrice.getBeer().equals(beer)) {
				BeerPrice copy = beerPrice.clone();
				iterator.remove();
				beerPrice.getBeer().getPrices().remove(beerPrice);
				beerPrice.setStore(null);
				beerPrice.setBeer(null);
				return copy;
			}
		}
		return null;
	}

	public boolean deleteAllPrices() {
		for (Iterator<BeerPrice> iterator = prices.iterator();
			 iterator.hasNext(); ) {
			BeerPrice beerPrice = iterator.next();
			iterator.remove();
			beerPrice.getStore().getPrices().remove(beerPrice);
			beerPrice.setStore(null);
			beerPrice.setBeer(null);
		}
		return true;
	}

	public Optional<BeerPrice> findBeer(Long beerId) {
		for (BeerPrice beer : prices) {
			if (beer.getBeer().getId() == beerId) {
				return Optional.of(beer);
			}
		}
		return Optional.empty();
	}
}
