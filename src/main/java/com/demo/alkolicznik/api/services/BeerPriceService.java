package com.demo.alkolicznik.api.services;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.exceptions.classes.EntitiesNotFoundException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beerprice.BeerPriceNotFoundException;
import com.demo.alkolicznik.exceptions.classes.store.StoreNotFoundException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.ModelDtoConverter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeerPriceService {

	private StoreRepository storeRepository;

	private BeerRepository beerRepository;

	public BeerPriceService(StoreRepository storeRepository, BeerRepository beerRepository) {
		this.storeRepository = storeRepository;
		this.beerRepository = beerRepository;
	}

	public BeerPriceResponseDTO addByObject(Long storeId, BeerPriceRequestDTO beerPriceRequestDTO) {
		String beerFullname = beerPriceRequestDTO.getBeerName();
		double volume = beerPriceRequestDTO.getBeerVolume();
		// Fetch both store and beer from repositories.
		if (!storeRepository.existsById(storeId) && !beerRepository
				.existsByFullnameAndVolume(beerFullname, volume)) {
			throw new EntitiesNotFoundException(beerFullname, volume, storeId);
		}
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new StoreNotFoundException(storeId)
		);
		// Check if beer exists by fullname
		// If beer is not found in DB, then the reason is volume
		Beer beer = beerRepository.findByFullnameAndVolume(beerFullname, volume).orElseThrow(
				() -> new BeerNotFoundException(beerFullname, volume)
		);
		if (store.findBeer(beer.getId()).isPresent()) {
			throw new BeerPriceAlreadyExistsException();
		}
		// Pass beer with price to store and save changes.
		double price = beerPriceRequestDTO.getPrice();
		store.saveBeer(beer, price);
		storeRepository.save(store);
		return new BeerPriceResponseDTO(store.findBeer(beer.getId()).get());
	}

	// No annotation will throw Hibernate lazy-loading exception (on GUI)
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, noRollbackFor = Exception.class)
	public BeerPriceResponseDTO addByParam(Long storeId, Long beerId, Double price) {
		throwExceptionIfBothStoreAndBeerNotFound(storeId, beerId);
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new StoreNotFoundException(storeId)
		);
		Beer beer = beerRepository.findById(beerId).orElseThrow(
				() -> new BeerNotFoundException(beerId)
		);
		if (store.findBeer(beer.getId()).isPresent()) {
			throw new BeerPriceAlreadyExistsException();
		}
		store.saveBeer(beer, price);
		storeRepository.save(store);
		return new BeerPriceResponseDTO(store.findBeer(beerId).get());
	}

	public List<BeerPriceResponseDTO> getAllByStoreId(Long storeId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId));
		Set<BeerPrice> prices = new TreeSet<>(comparatorByBeerIdAndPrice());
		prices.addAll(store.getPrices());
		return ModelDtoConverter.beerPriceSetToDtoListKeepOrder(prices);
	}

	public BeerPriceResponseDTO get(Long storeId, Long beerId) {
		throwExceptionIfBothStoreAndBeerNotFound(storeId, beerId);
		Store store = storeRepository.findById(storeId).orElseThrow(
				() -> new StoreNotFoundException(storeId)
		);
		if (!beerRepository.existsById(beerId)) {
			throw new BeerNotFoundException(beerId);
		}
		return new BeerPriceResponseDTO(store.findBeer(beerId).orElseThrow(
				() -> new BeerPriceNotFoundException()
		));
	}

	public List<BeerPriceResponseDTO> getAll() {
		List<Store> stores = storeRepository.findAll();
		Set<BeerPrice> prices = new TreeSet<>(comparatorByCityBeerIdPriceAndStoreId());
		stores.forEach(store -> prices.addAll(store.getPrices()));
		return ModelDtoConverter.beerPriceSetToDtoListKeepOrder(prices);
	}

	public List<BeerPriceResponseDTO> getAllByCity(String city) {
		List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);

		if (cityStores.isEmpty()) {
			throw new NoSuchCityException(city);
		}
		Set<BeerPrice> prices = new TreeSet<>(comparatorByBeerIdPriceAndStoreId());
		cityStores.forEach(store -> prices.addAll(store.getPrices()));
		return ModelDtoConverter.beerPriceSetToDtoListKeepOrder(prices);
	}

	public List<BeerPriceResponseDTO> getAllByBeerId(Long beerId) {
		Beer beer = beerRepository.findById(beerId).orElseThrow(
				() -> new BeerNotFoundException(beerId)
		);
		List<Store> stores = storeRepository.findAll();

		Set<BeerPrice> prices = new TreeSet<>(comparatorByCityPriceAndStoreId());
		stores.forEach(store -> store.findBeer(beerId).ifPresent(price -> prices.add(price)));
		return ModelDtoConverter.beerPriceSetToDtoListKeepOrder(prices);
	}

	public List<BeerPriceResponseDTO> getAllByBeerIdAndCity(Long beerId, String city) {
		if (!beerRepository.existsById(beerId) && !storeRepository.existsByCity(city)) {
			throw new EntitiesNotFoundException(beerId, city);
		}
		Beer beer = beerRepository.findById(beerId).orElseThrow(
				() -> new BeerNotFoundException(beerId)
		);
		if (!storeRepository.existsByCity(city)) {
			throw new NoSuchCityException(city);
		}
		Set<BeerPrice> beerPricesInCity = new TreeSet<>(comparatorByPriceAndStoreId());
		beer.getPrices().stream()
				.filter(price -> price.getStore().getCity().equals(city))
				.forEach(price -> beerPricesInCity.add(price));
		return ModelDtoConverter.beerPriceSetToDtoListKeepOrder(beerPricesInCity);
	}

	public BeerPriceResponseDTO update(Long storeId, Long beerId, Double price) {
		Store store = storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId));
		if (!beerRepository.existsById(beerId)) {
			throw new BeerNotFoundException(beerId);
		}
		BeerPrice beerPrice = store.findBeer(beerId).orElseThrow(() ->
				new BeerPriceNotFoundException());

		MonetaryAmount updatedPrice = Monetary.getDefaultAmountFactory()
				.setCurrency("PLN").setNumber(price).create();
		beerPrice.setPrice(updatedPrice);

		storeRepository.save(store);
		return new BeerPriceResponseDTO(beerPrice);
	}

	// No annotation throws Hibernation lazy-loading exception (on GUI)
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, noRollbackFor = Exception.class)
	public BeerPriceDeleteDTO delete(Long storeId, Long beerId) {
		Store store = storeRepository.findById(storeId).orElseThrow(() ->
				new StoreNotFoundException(storeId));
		Beer beer = beerRepository.findById(beerId).orElseThrow(() ->
				new BeerNotFoundException(beerId));
		BeerPrice beerPrice = store.findBeer(beerId).orElseThrow(() ->
				new BeerPriceNotFoundException());
		BeerPrice deleted = store.deleteBeer(beer);
		beerRepository.save(beer);
		storeRepository.save(store);
		return new BeerPriceDeleteDTO(deleted);
	}

	private Set<BeerPriceResponseDTO> mapToDto(Set<BeerPrice> beerPrices) {
		return beerPrices.stream()
				.map(BeerPriceResponseDTO::new)
				.collect(Collectors.toUnmodifiableSet());
	}

	private Comparator comparatorByBeerIdPriceAndStoreId() {
		return Comparator.comparing(p -> ((BeerPrice) p).getBeer().getId())
				.thenComparing(p -> ((BeerPrice) p).getAmountOnly())
				.thenComparing(p -> ((BeerPrice) p).getStore().getId());
	}

	private Comparator comparatorByCityPriceAndStoreId() {
		return Comparator.comparing(p -> ((BeerPrice) p).getStore().getCity())
				.thenComparing(p -> ((BeerPrice) p).getAmountOnly())
				.thenComparing(p -> ((BeerPrice) p).getStore().getId());
	}

	private Comparator comparatorByPriceAndStoreId() {
		return Comparator.comparing(p -> ((BeerPrice) p).getAmountOnly())
				.thenComparing(p -> ((BeerPrice) p).getStore().getId());
	}

	private Comparator comparatorByBeerIdAndPrice() {
		return Comparator.comparing(p -> ((BeerPrice) p).getBeer().getId())
				.thenComparing(p -> ((BeerPrice) p).getAmountOnly());
	}

	private Comparator comparatorByCityBeerIdPriceAndStoreId() {
		return Comparator.comparing(p -> ((BeerPrice) p).getStore().getCity())
				.thenComparing(p -> ((BeerPrice) p).getBeer().getId())
				.thenComparing(p -> ((BeerPrice) p).getAmountOnly())
				.thenComparing(p -> ((BeerPrice) p).getStore().getId());
	}

	private void throwExceptionIfBothStoreAndBeerNotFound(Long storeId, Long beerId) {
		if (!storeRepository.existsById(storeId) && !beerRepository.existsById(beerId)) {
			throw new EntitiesNotFoundException(beerId, storeId);
		}
	}
}
