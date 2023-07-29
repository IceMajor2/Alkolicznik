package com.demo.alkolicznik.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;

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
		beer.setType(beerRequestDTO.getType());
		beer.setVolume(beerRequestDTO.getVolume());
		return beer;
	}

	public static List<BeerPriceResponseDTO> beerPriceSetToDtoListKeepOrder(Set<BeerPrice> prices) {
		return prices.stream()
				.map(BeerPriceResponseDTO::new)
				.toList();
	}
}
