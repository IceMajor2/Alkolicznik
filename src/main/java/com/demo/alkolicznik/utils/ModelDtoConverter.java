package com.demo.alkolicznik.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
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
		image.ifPresent(store::setImage);
		return store;
	}

	public static List<BeerPriceResponseDTO> beerPriceSetToDtoListKeepOrder(Set<BeerPrice> prices) {
		return prices.stream()
				.map(BeerPriceResponseDTO::new)
				.toList();
	}

	public static List<ImageResponseDTO> storeImageListToDtoList(List<StoreImage> storeImages) {
		return storeImages.stream()
				.map(ImageResponseDTO::new)
				.toList();
	}

	public static List<ImageResponseDTO> beerImageListToDtoList(List<BeerImage> beerImages) {
		return beerImages.stream()
				.map(ImageResponseDTO::new)
				.toList();
	}

	public static BeerUpdateDTO convertToUpdate(BeerRequestDTO beer) {
		BeerUpdateDTO beerUpdate = new BeerUpdateDTO();
		beerUpdate.setBrand(beer.getBrand());
		beerUpdate.setType(beer.getType());
		beerUpdate.setVolume(beer.getVolume());
		return beerUpdate;
	}

	public static BeerRequestDTO convertToRequest(BeerResponseDTO beerResponse) {
		BeerRequestDTO beerRequest = new BeerRequestDTO();
		beerRequest.setBrand(beerResponse.getBrand());
		beerRequest.setType(beerResponse.getType());
		beerRequest.setVolume(beerResponse.getVolume());
		return beerRequest;
	}
}
