package com.demo.alkolicznik.utils;

import java.util.ArrayList;
import java.util.List;

import com.demo.alkolicznik.api.BeerImageTest;
import com.demo.alkolicznik.api.StoreImageTest;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.ImageResponseDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.ImageModel;
import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	private static ObjectMapper mapper = new ObjectMapper();

	public static StoreRequestDTO createStoreRequest(String name, String city, String street) {
		StoreRequestDTO request = new StoreRequestDTO();
		request.setName(name);
		request.setCity(city);
		request.setStreet(street);
		return request;
	}

	public static StoreRequestDTO createStoreRequest(Store store) {
		return createStoreRequest(store.getName(), store.getCity(), store.getStreet());
	}

	public static StoreResponseDTO createStoreResponse(Integer id, String name,
			String city, String street, ImageResponseDTO imageDTO) {
		StoreResponseDTO store = new StoreResponseDTO();
		store.setId(id.longValue());
		store.setName(name);
		store.setCity(city);
		store.setStreet(street);
		store.setImage(imageDTO);
		return store;
	}

	public static StoreResponseDTO createStoreResponse(Integer id, String name,
			String city, String street) {
		return createStoreResponse(id, name, city, street, null);
	}

	public static StoreResponseDTO createStoreResponse(Store store) {
		return createStoreResponse(store.getId().intValue(), store.getName(),
				store.getCity(), store.getStreet(), createImageResponse(store.getImage().orElse(null)));
	}

	public static BeerResponseDTO createBeerResponse(long id, String brand, String type, Double volume, ImageResponseDTO imageDTO) {
		BeerResponseDTO response = new BeerResponseDTO();
		response.setId(id);
		response.setBrand(brand);
		response.setType(type);
		response.setVolume(volume);
		response.setImage(imageDTO);
		return response;
	}

	public static BeerResponseDTO createBeerResponse(long id, String brand, String type, Double volume) {
		return createBeerResponse(id, brand, type, volume, null);
	}

	public static BeerResponseDTO createBeerResponse(Beer beer) {
		return createBeerResponse(
				beer.getId(), beer.getBrand(), beer.getType(), beer.getVolume(),
				createImageResponse(beer.getImage().orElse(null))
		);
	}

	public static BeerResponseDTO createBeerResponse(Beer beer, ImageResponseDTO image) {
		return createBeerResponse(beer.getId(), beer.getBrand(), beer.getType(), beer.getVolume(),
				image);
	}

	public static ImageRequestDTO createImageRequest(String srcPath) {
		ImageRequestDTO request = new ImageRequestDTO();
		request.setImagePath(srcPath);
		return request;
	}

	public static ImageResponseDTO createImageResponse(String filename, ImageResponseDTO actual, Class<? extends ImageModel> imgClass) {
		return createImageResponse(filename, null, actual, imgClass);
	}

	public static ImageResponseDTO createImageResponse(String filename, String storeName, ImageResponseDTO actual, Class<? extends ImageModel> imgClass) {
		ImageResponseDTO response = new ImageResponseDTO();
		response.setImageUrl(
				(imgClass.equals(StoreImage.class)
						? StoreImageTest.IMG_TRANSFORMED_URL : imgClass.equals(BeerImage.class)
						? BeerImageTest.IMG_TRANSFORMED_URL : null)
						+ filename
		);
		if (imgClass.equals(StoreImage.class)) {
			response.setImageUrl(response.getImageUrl()
					+ "?updatedAt=" + fetchEpochsInUpdatedAt(actual.getImageUrl()));
		}
		if (actual.getRemoteId() != null) {
			response.setRemoteId(actual.getRemoteId());
		}
		response.setStoreName(storeName);
		return response;
	}

	private static String fetchEpochsInUpdatedAt(String url) {
		return url.substring(url.lastIndexOf('=') + 1);
	}

	public static ImageResponseDTO createImageResponse(String filename, String remoteId, Class<? extends ImageModel> imgClass) {
		ImageResponseDTO response = new ImageResponseDTO();
		response.setImageUrl(
				(imgClass.equals(StoreImage.class)
						? StoreImageTest.IMG_TRANSFORMED_URL : imgClass.equals(BeerImage.class)
						? BeerImageTest.IMG_TRANSFORMED_URL : null) + filename
		);
		response.setRemoteId(remoteId);
		return response;
	}

	public static ImageResponseDTO createImageResponse(ImageModel image) {
		if(image != null) {
			return new ImageResponseDTO(image);
		}
		return null;
	}

	public static BeerRequestDTO createBeerRequest(String brand, String type, Double volume) {
		BeerRequestDTO request = new BeerRequestDTO();
		request.setBrand(brand);
		request.setType(type);
		request.setVolume(volume);
		return request;
	}

	public static BeerRequestDTO createBeerRequest(Beer beer) {
		BeerRequestDTO request =
				createBeerRequest(beer.getBrand(), beer.getType(), beer.getVolume());
		return request;
	}

	public static BeerDeleteRequestDTO createBeerDeleteRequest(String brand, String type, Double volume) {
		BeerDeleteRequestDTO request = new BeerDeleteRequestDTO();
		request.setBrand(brand);
		request.setType(type);
		request.setVolume(volume);
		return request;
	}

	public static BeerDeleteRequestDTO createBeerDeleteRequest(Beer beer) {
		return createBeerDeleteRequest(beer.getBrand(), beer.getType(), beer.getVolume());
	}

	public static BeerPriceRequestDTO createBeerPriceRequest(String beerName, Double volume, Double price) {
		BeerPriceRequestDTO request = new BeerPriceRequestDTO();
		request.setBeerName(beerName);
		if (volume != null) {
			request.setBeerVolume(volume);
		}
		request.setPrice(price);
		return request;
	}

	public static BeerPriceRequestDTO createBeerPriceRequest(BeerPrice beerPrice) {
		return createBeerPriceRequest(beerPrice.getBeer().getFullName(),
				beerPrice.getBeer().getVolume(),
				beerPrice.getPrice().getNumber().doubleValueExact());
	}

	public static BeerPriceResponseDTO createBeerPriceResponse(BeerResponseDTO beerResponseDTO,
			StoreResponseDTO storeResponseDTO, String price) {
		BeerPriceResponseDTO response = new BeerPriceResponseDTO();
		response.setBeer(beerResponseDTO);
		response.setStore(storeResponseDTO);
		response.setPrice(price);
		return response;
	}

	public static BeerPriceResponseDTO createBeerPriceResponse(Beer beer, Store store, String price) {
		BeerResponseDTO beerDTO = createBeerResponse(beer);
		StoreResponseDTO storeDTO = createStoreResponse(store);
		return createBeerPriceResponse(beerDTO, storeDTO, price);
	}

	public static BeerUpdateDTO createBeerUpdateRequest(String brand, String type, Double volume) {
		BeerUpdateDTO request = new BeerUpdateDTO();
		request.setBrand(brand);
		request.setType(type);
		request.setVolume(volume);
		return request;
	}

	public static BeerPriceUpdateDTO createBeerPriceUpdateRequest(Double price) {
		BeerPriceUpdateDTO request = new BeerPriceUpdateDTO();
		request.setPrice(price);
		return request;
	}

	public static BeerDeleteResponseDTO createBeerDeleteResponse(Beer beer, String status) {
		return new BeerDeleteResponseDTO(beer, status);
	}

	public static StoreDeleteDTO createStoreDeleteResponse(Store store, String status) {
		return new StoreDeleteDTO(store, status);
	}

	public static StoreUpdateDTO createStoreUpdateRequest(String name, String city, String street) {
		StoreUpdateDTO request = new StoreUpdateDTO();
		request.setName(name);
		request.setCity(city);
		request.setStreet(street);
		return request;
	}

	public static BeerPriceDeleteDTO createBeerPriceDeleteResponse(BeerResponseDTO beerDTO,
			StoreResponseDTO storeDTO,
			String price,
			String status) {
		BeerPriceDeleteDTO response = new BeerPriceDeleteDTO();
		response.setBeer(beerDTO);
		response.setStore(storeDTO);
		response.setPrice(price);
		response.setStatus(status);
		return response;
	}

	public static BeerPriceDeleteDTO createBeerPriceDeleteResponse(Beer beer, Store store, String price, String status) {
		BeerResponseDTO beerResponseDTO = createBeerResponse(beer);
		StoreResponseDTO storeResponseDTO = createStoreResponse(store);
		return createBeerPriceDeleteResponse(beerResponseDTO, storeResponseDTO, price, status);
	}

	public static SignupRequestDTO createUserRequest(String username, String password) {
		SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
		signupRequestDTO.setUsername(username);
		signupRequestDTO.setPassword(password);
		return signupRequestDTO;
	}

	public static ImageDeleteDTO createImageDeleteResponse(Beer beer, String message) {
		ImageDeleteDTO deleteDTO = new ImageDeleteDTO();
		deleteDTO.setBeer(createBeerResponse(beer));
		deleteDTO.setMessage(message);
		return deleteDTO;
	}

	public static <T> List<T> toModelList(String json, Class<T> clazz) {
		JSONArray array = getJsonArray(json);

		List<T> tObjects = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			try {
				String objectAsString = array.getString(i);
				T object = toModel(objectAsString, clazz);
				tObjects.add(object);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return tObjects;
	}

	/**
	 * Converts a model, object to a JSON string.
	 *
	 * @param model some class object
	 * @return JSON {@code String}
	 */
	public static String toJsonString(Object model) {
		try {
			String modelAsString = mapper.writeValueAsString(model);
			return modelAsString;
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts JSON string to a desired model (if JSON matches it).
	 *
	 * @param json  JSON as {@code String}
	 * @param clazz class of model-representing JSON
	 * @return object of provided class
	 */
	public static <T> T toModel(String json, Class<T> clazz) {
		try {
			T model = mapper.readValue(json, clazz);
			return model;
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static JSONObject getJsonObject(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject;
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static JSONArray getJsonArray(String json) {
		try {
			JSONArray array = new JSONArray(json);
			return array;
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
