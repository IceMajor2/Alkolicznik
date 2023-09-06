package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.api.BeerImageTest;
import com.demo.alkolicznik.api.StoreImageTest;
import com.demo.alkolicznik.dto.beer.*;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceRequestDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.models.image.StoreImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static AuthRequestDTO createAuthRequest(String username, String password) {
        return new AuthRequestDTO(username, password);
    }

    public static SignupRequestDTO createSignupRequest(String username, String password) {
        return new SignupRequestDTO(username, password);
    }

    public static StoreRequestDTO createStoreRequest(String name, String city, String street) {
        StoreRequestDTO request = new StoreRequestDTO();
        request.setName(name);
        request.setCity(city);
        request.setStreet(street);
        return request;
    }

    public static StoreResponseDTO createStoreResponse(Integer id, String name,
                                                       String city, String street, StoreImageResponseDTO imageDTO) {
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

    public static BeerResponseDTO createBeerResponse(long id, String brand, String type, Double volume, BeerImageResponseDTO imageDTO) {
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

    public static ImageRequestDTO createImageRequest(String srcPath) {
        ImageRequestDTO request = new ImageRequestDTO();
        request.setImagePath(srcPath);
        return request;
    }

    public static StoreImageResponseDTO createImageResponse(String filename, String storeName, BeerImageResponseDTO actual) {
        StoreImageResponseDTO response = new StoreImageResponseDTO();
        response.setStoreName(storeName);
        response.setUrl(StoreImageTest.IMG_TRANSFORMED_URL + filename
                + "?updatedAt=" + fetchEpochsInUpdatedAt(actual.getUrl()));
//        response.setRemoteId(actual.getRemoteId());
        return response;
    }

    public static BeerImageResponseDTO createBeerImageResponse(String filename, String remoteId) {
        BeerImageResponseDTO response = new BeerImageResponseDTO();
        response.setUrl(BeerImageTest.IMG_TRANSFORMED_URL + filename);
        response.setRemoteId(remoteId);
        return response;
    }

    public static StoreImageResponseDTO createStoreImageResponse(String filename, String remoteId) {
        StoreImageResponseDTO response = new StoreImageResponseDTO();
        response.setUrl(StoreImageTest.IMG_TRANSFORMED_URL + filename);
        response.setRemoteId(remoteId);
        return response;
    }

    private static String fetchEpochsInUpdatedAt(String url) {
        return url.substring(url.lastIndexOf('=') + 1);
    }

    public static StoreImageResponseDTO createImageResponse(StoreImage storeImage) {
        if (storeImage != null) {
            return new StoreImageResponseDTO(storeImage);
        }
        return null;
    }

    public static BeerImageResponseDTO createImageResponse(BeerImage beerImage) {
        if (beerImage != null) {
            return new BeerImageResponseDTO(beerImage);
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

    public static BeerDeleteRequestDTO createBeerDeleteRequest(String brand, String type, Double volume) {
        BeerDeleteRequestDTO request = new BeerDeleteRequestDTO();
        request.setBrand(brand);
        request.setType(type);
        request.setVolume(volume);
        return request;
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

    public static BeerPriceResponseDTO createBeerPriceResponse(BeerResponseDTO beerResponseDTO,
                                                               StoreResponseDTO storeResponseDTO, String price) {
        BeerPriceResponseDTO response = new BeerPriceResponseDTO();
        response.setBeer(beerResponseDTO);
        response.setStore(storeResponseDTO);
        response.setPrice(price);
        return response;
    }

    public static BeerUpdateDTO createBeerUpdateRequest(String brand, String type, Double volume) {
        BeerUpdateDTO request = new BeerUpdateDTO();
        request.setBrand(brand);
        request.setType(type);
        request.setVolume(volume);
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

    public static <T> List<T> toModelList(String json, Class<T> clazz) {
        JSONArray array = getJsonArray(json);

        List<T> tObjects = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                String objectAsString = array.getString(i);
                T object = toModel(objectAsString, clazz);
                tObjects.add(object);
            } catch (JSONException e) {
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
        } catch (JsonProcessingException e) {
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static JSONObject getJsonObject(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONArray getJsonArray(String json) {
        try {
            JSONArray array = new JSONArray(json);
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
