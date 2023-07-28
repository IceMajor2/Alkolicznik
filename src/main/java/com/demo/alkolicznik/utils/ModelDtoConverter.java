package com.demo.alkolicznik.utils;

import java.util.Collection;
import java.util.List;

import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Beer;
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
}
