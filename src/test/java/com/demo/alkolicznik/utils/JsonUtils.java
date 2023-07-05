package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.dto.*;
import com.demo.alkolicznik.dto.delete.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.delete.StoreDeleteResponseDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static StoreRequestDTO createStoreRequest(String name, String city, String street) {
        StoreRequestDTO request = new StoreRequestDTO();
        request.setName(name);
        request.setCity(city);
        request.setStreet(street);
        return request;
    }

    public static StoreResponseDTO createStoreResponse(Long id, String name, String city, String street) {
        StoreResponseDTO store = new StoreResponseDTO();
        store.setId(id);
        store.setName(name);
        store.setCity(city);
        store.setStreet(street);
        return store;
    }

    public static StoreResponseDTO createStoreResponse(Store store) {
        return createStoreResponse(store.getId(), store.getName(), store.getCity(), store.getStreet());
    }

    public static BeerResponseDTO createBeerResponse(Long id, String name, Double volume) {
        BeerResponseDTO response = new BeerResponseDTO();
        response.setId(id);
        response.setFullName(name);
        response.setVolume(volume);
        return response;
    }

    public static BeerResponseDTO createBeerResponse(Beer beer) {
        return createBeerResponse(beer.getId(), beer.getFullName(), beer.getVolume());
    }

    public static BeerRequestDTO createBeerRequest(String brand, String type, Double volume) {
        BeerRequestDTO request = new BeerRequestDTO();
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
                                                               StoreResponseDTO storeResponseDTO,
                                                               String price) {
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

    public static BeerPriceUpdateDTO createBeerPriceUpdateRequest(Double price) {
        BeerPriceUpdateDTO request = new BeerPriceUpdateDTO();
        request.setPrice(price);
        return request;
    }

    public static BeerDeleteResponseDTO createBeerDeleteResponse(Beer beer, String status) {
        return new BeerDeleteResponseDTO(beer, status);
    }

    public static StoreDeleteResponseDTO createStoreDeleteResponse(Store store, String status) {
        return new StoreDeleteResponseDTO(store, status);
    }

    public static StoreUpdateDTO createStoreUpdateRequest(String name, String city, String street) {
        StoreUpdateDTO request = new StoreUpdateDTO();
        request.setName(name);
        request.setCity(city);
        request.setStreet(street);
        return request;
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
