package com.demo.alkolicznik.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;

public class ModelDtoConverter {

	public static List<BeerResponseDTO> beerListToDtoList(Collection<Beer> beers) {
		return beers.stream()
				.map(BeerResponseDTO::new)
				.toList();
	}

	public static List<StoreResponseDTO> storeListToDtoList(Collection<Store> stores) {
		return stores.stream()
				.map(StoreResponseDTO::new)
				.toList();
	}

	public static Beer convertToModelNoImage(BeerRequestDTO beerRequestDTO) {
		Beer beer = new Beer();
		beer.setBrand(beerRequestDTO.getBrand());
		if (beerRequestDTO.getType() != null && beerRequestDTO.getType().isBlank()) {
			beer.setType(null);
		}
		else {
			beer.setType(beerRequestDTO.getType());
		}
		if (beerRequestDTO.getVolume() == null) {
			beer.setVolume(0.5);
		}
		else {
			beer.setVolume(beerRequestDTO.getVolume());
		}
		return beer;
	}

	public static Store convertToModelNoImage(StoreRequestDTO storeRequestDTO) {
		Store store = new Store();
		store.setName(storeRequestDTO.getName());
		store.setCity(storeRequestDTO.getCity());
		store.setStreet(storeRequestDTO.getStreet());
		return store;
	}

	public static Store convertToModelWithImage(StoreRequestDTO storeRequestDTO, Optional<StoreImage> image) {
		Store store = convertToModelNoImage(storeRequestDTO);
		image.ifPresent(storeImage -> store.setImage(storeImage));
		return store;
	}

	public static List<BeerPriceResponseDTO> beerPriceSetToDtoListKeepOrder(Set<BeerPrice> prices) {
		return prices.stream()
				.map(BeerPriceResponseDTO::new)
				.toList();
	}
}
