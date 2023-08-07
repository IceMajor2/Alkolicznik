package com.demo.alkolicznik.api.services;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.exceptions.classes.FileNotFoundException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerAlreadyExistsException;
import com.demo.alkolicznik.exceptions.classes.beer.BeerNotFoundException;
import com.demo.alkolicznik.exceptions.classes.NoSuchCityException;
import com.demo.alkolicznik.exceptions.classes.ObjectsAreEqualException;
import com.demo.alkolicznik.exceptions.classes.PropertiesMissingException;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.repositories.BeerRepository;
import com.demo.alkolicznik.repositories.StoreRepository;
import com.demo.alkolicznik.utils.ModelDtoConverter;
import com.vaadin.flow.component.html.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeerService {

	private BeerRepository beerRepository;

	private StoreRepository storeRepository;

	private ImageService imageService;

	@Autowired
	public BeerService(BeerRepository beerRepository, StoreRepository storeRepository, ImageService imageService) {
		this.beerRepository = beerRepository;
		this.storeRepository = storeRepository;
		this.imageService = imageService;
	}

	public BeerResponseDTO get(Long beerId) {
		Beer beer = beerRepository.findById(beerId).orElseThrow(
				() -> new BeerNotFoundException(beerId)
		);
		return new BeerResponseDTO(beer);
	}

	public List<BeerResponseDTO> getBeers(String city) {
		if (!storeRepository.existsByCity(city)) {
			throw new NoSuchCityException(city);
		}
		List<Store> cityStores = storeRepository.findAllByCityOrderByIdAsc(city);
		// order by id ascending
		Set<Beer> beersInCity = new TreeSet<>(Comparator.comparing(Beer::getId));
		for (Store store : cityStores) {
			beersInCity.addAll(
					store.getPrices().stream()
							.map(BeerPrice::getBeer)
							.toList()
			);
		}
		return ModelDtoConverter.beerListToDtoList(beersInCity);
	}

	public List<BeerResponseDTO> getBeers() {
		return ModelDtoConverter.beerListToDtoList(beerRepository.findAllByOrderByIdAsc());
	}

	// TODO: probably should be a transaction
	public BeerResponseDTO add(BeerRequestDTO requestDTO) {
		Beer beer = ModelDtoConverter.convertToModelNoImage(requestDTO);

		if (beerRepository.exists(beer)) {
			throw new BeerAlreadyExistsException();
		}
		// After beer's validation, we can begin (if passed)
		// the uploading of image and attaching it to beer object.
		String imagePath = requestDTO.getImagePath();
		if (imagePath != null) {
			imageService.add(beer, imagePath);
		}
		return new BeerResponseDTO(beerRepository.save(beer));
	}

	public BeerResponseDTO replace(Long beerId, BeerRequestDTO requestDTO) {
		Beer toOverwrite = checkForPutConditions(beerId, requestDTO);
		Beer newBeer = ModelDtoConverter.convertToModelNoImage(requestDTO);
		Beer overwritten = updateFieldsOnPut(toOverwrite, newBeer);

		if (beerRepository.exists(overwritten)) {
			throw new BeerAlreadyExistsException();
		}
		// for each PUT request all the previous
		// beer prices for this beer MUST be deleted
		toOverwrite.deleteAllPrices();
		// for each PUT request the previous image MUST be deleted
		if (toOverwrite.getImage().isPresent()) {
			imageService.delete(toOverwrite);
		}
		// after deleting previous image, check if there is a replacement
		// if yes, execute the whole 'addImage' procedure
		String imagePath = requestDTO.getImagePath();
		if (imagePath != null) {
			imageService.add(toOverwrite, imagePath);
		}
		return new BeerResponseDTO(beerRepository.save(toOverwrite));
	}

	public BeerResponseDTO update(Long beerId, BeerUpdateDTO updateDTO) {
		Beer beer = checkForPatchConditions(beerId, updateDTO);
		Beer updated = updateFieldsOnPatch(beer, updateDTO);

		if (beerRepository.exists(updated) && updateDTO.getImagePath() == null) {
			throw new BeerAlreadyExistsException();
		}
		// deleting prices on conditions
		if (this.pricesToDelete(updateDTO)) {
			beer.deleteAllPrices();
		}
		// deleting image on conditions and
		// reuploading if it was requested
		if (this.imageToDelete(updated, updateDTO)) {
			updateImage(updated, updateDTO);
		}
		return new BeerResponseDTO(beerRepository.save(beer));
	}

	public BeerDeleteResponseDTO delete(Long beerId) {
		Beer toDelete = beerRepository.findById(beerId).orElseThrow(() ->
				new BeerNotFoundException(beerId));
		BeerDeleteResponseDTO deleteResponse = new BeerDeleteResponseDTO(toDelete);
		beerRepository.delete(toDelete);
		if (toDelete.getImage().isPresent()) {
			imageService.delete(toDelete);
		}
		return deleteResponse;
	}

	public BeerDeleteResponseDTO delete(BeerDeleteRequestDTO request) {
		Beer toDelete = beerRepository.findByFullnameAndVolume(request.getFullName(), request.getVolume())
				.orElseThrow(() -> new BeerNotFoundException(request.getFullName(), request.getVolume()));
		return this.delete(toDelete.getId());
	}

	public Image getImageComponent(Long beerId) {
		return imageService.getVaadinBeerImage(beerId);
	}

	private Beer updateFieldsOnPatch(Beer toUpdate, BeerUpdateDTO updateDTO) {
		String updatedBrand = updateDTO.getBrand();
		String updatedType = updateDTO.getType();
		Double updatedVolume = updateDTO.getVolume();

		if (updatedBrand != null) {
			toUpdate.setBrand(updatedBrand);
		}
		if (updatedType != null) {
			if (updatedType.isBlank()) toUpdate.setType(null);
			else toUpdate.setType(updatedType);
		}
		if (updatedVolume != null) {
			toUpdate.setVolume(updatedVolume);
		}
		return toUpdate;
	}

	private Beer updateFieldsOnPut(Beer toOverwrite, Beer newBeer) {
		toOverwrite.setBrand(newBeer.getBrand());
		toOverwrite.setType(newBeer.getType());
		toOverwrite.setVolume(newBeer.getVolume());
		return toOverwrite;
	}

	private Beer checkForUpdateConditions(Long beerId, BeerUpdateDTO updateDTO) {
		Beer beer = beerRepository.findById(beerId).orElseThrow(
				() -> new BeerNotFoundException(beerId)
		);
		if (!updateDTO.anythingToUpdate(beer)) {
			throw new ObjectsAreEqualException();
		}
		return beer;
	}

	private Beer checkForPatchConditions(Long beerId, BeerUpdateDTO updateDTO) {
		if (updateDTO.propertiesMissing()) {
			throw new PropertiesMissingException();
		}
		return checkForUpdateConditions(beerId, updateDTO);
	}

	private Beer checkForPutConditions(Long beerId, BeerRequestDTO requestDTO) {
		String imagePath = requestDTO.getImagePath();
		if(imagePath != null) {
			File file = new File(imagePath);
			if(!file.exists()) {
				throw new FileNotFoundException(imagePath);
			}
		}
		return checkForUpdateConditions(beerId, new BeerUpdateDTO(requestDTO));
	}

	private void updateImage(Beer toUpdate, BeerUpdateDTO updateDTO) {
		if(toUpdate.getImage().isPresent()) {
			imageService.delete(toUpdate);
		}
		String imagePath = updateDTO.getImagePath();
		if (imagePath != null) {
			imageService.add(toUpdate, imagePath);
		}
	}

	private boolean imageToDelete(Beer toUpdate, BeerUpdateDTO updateDTO) {
		// if updated field is *ONLY* volume, then do not delete image
		if (updateDTO.getBrand() == null
				&& updateDTO.getType() == null
				&& updateDTO.getImagePath() == null) {
			return false;
		}
		if (toUpdate.getImage().isPresent()) {
			return true;
		}
		if(updateDTO.getImagePath() != null) {
			return true;
		}
		return false;
	}

	private boolean pricesToDelete(BeerUpdateDTO updateDTO) {
		return updateDTO.getBrand() != null || updateDTO.getType() != null;
	}
}
