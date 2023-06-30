package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.*;
import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class TestUtils {

    private static TestRestTemplate restTemplate;
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        TestUtils.restTemplate = restTemplate;
    }

    /**
     * Create {@code StoreRequestDTO}. Used mainly for hardcoding the expected response in tests.
     *
     * @param name
     * @return {@code StoreRequestDTO}
     */
    public static StoreRequestDTO createStoreRequest(String name, String city, String street) {
        StoreRequestDTO request = new StoreRequestDTO();
        request.setName(name);
        request.setCity(city);
        request.setStreet(street);
        return request;
    }

    public static Store createStoreResponse(Long id, String name, String city, String street) {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setCity(city);
        store.setStreet(street);
        return store;
    }

    /**
     * Create {@code BeerResponseDTO}. Used mainly for hardcoding the expected
     * response in tests. If you don't want to specify some parameter,
     * just replace it with {@code null}.
     *
     * @param id
     * @param name
     * @param volume
     * @return {@code BeerResponseDTO}
     */
    public static BeerResponseDTO createBeerResponseDTO(Long id, String name, Double volume) {
        BeerResponseDTO response = new BeerResponseDTO();
        response.setId(id);
        response.setFullName(name);
        response.setVolume(volume);
        return response;
    }

    /**
     * Create {@code BeerRequestDTO}. Used mainly for hardcoding the expected
     * response in tests. If you don't want to specify some parameter,
     * just replace it with {@code null}.
     *
     * @param brand
     * @param type
     * @param volume
     * @return {@code BeerRequestDTO}
     */
    public static BeerRequestDTO createBeerRequestDTO(String brand, String type, Double volume) {
        BeerRequestDTO request = new BeerRequestDTO();
        request.setBrand(brand);
        request.setType(type);
        request.setVolume(volume);
        return request;
    }

    /**
     * Create {@code BeerPriceRequestDTO}. Used mainly for hardcoding the expected
     * response in tests. If you don't want to specify some parameter,
     * just replace it with {@code null}.
     *
     * @param beerName
     * @param price
     * @return {@code BeerPriceRequestDTO}
     */
    public static BeerPriceRequestDTO createBeerPriceRequest(String beerName, Double price) {
        BeerPriceRequestDTO request = new BeerPriceRequestDTO();
        request.setBeerName(beerName);
        request.setPrice(price);
        return request;
    }

    /**
     * Create {@code BeerPriceResponseDTO}. Used mainly for hardcoding the expected
     * response in tests. If you don't want to specify some parameter,
     * just replace it with {@code null}.
     *
     * @param storeId
     * @param storeName
     * @param beerId
     * @param beerName
     * @param price
     * @return {@code BeerPriceResponseDTO}
     */
    public static BeerPriceResponseDTO createBeerPriceResponse(Long storeId, String storeName,
                                                               Long beerId, String beerName, Double price) {
        BeerPriceResponseDTO response = new BeerPriceResponseDTO();
        response.setStoreId(storeId);
        response.setStoreName(storeName);
        response.setBeerId(beerId);
        response.setBeerName(beerName);
        response.setPrice(price);
        return response;
    }

    /**
     * Converts JSON array (as {@code String}) representing
     * {@code Store} to {@code java.util.List}.
     *
     * @param json JSON array
     * @return {@code List<Store>}
     */
    public static List<Store> toStoreList(String json) {
        JSONArray array = getJsonArray(json);

        List<Store> stores = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                String storeJson = array.getString(i);
                Store store = toModel(storeJson, Store.class);
                stores.add(store);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stores;
    }

    /**
     * Converts JSON array (as {@code String}) representing
     * {@code BeerResponseDTO} to {@code java.util.List}.
     *
     * @param json JSON array
     * @return {@code List<BeerResponseDTO>}
     */
    public static List<BeerResponseDTO> toBeerResponseDTOList(String json) {
        JSONArray array = getJsonArray(json);

        List<BeerResponseDTO> responseDTOs = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                String beerAsString = array.getString(i);
                BeerResponseDTO responseDTO = toModel(beerAsString, BeerResponseDTO.class);
                responseDTOs.add(responseDTO);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseDTOs;
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
     * Helper method for sending a {@code HTTP GET}
     * request through {@code TestRestTemplate}.
     *
     * @param url           url of target request
     * @param pathVariables url variables
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> getRequest(String url, Object... pathVariables) {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(url, String.class, pathVariables);
        return getResponse;
    }

    /**
     * Helper method for sending a {@code HTTP POST}
     * request through {@code TestRestTemplate}.
     *
     * @param url           url of target request
     * @param requestObject object to send to url
     * @param pathVariables url variables
     * @return
     */
    public static ResponseEntity<String> postRequest(String url, Object requestObject, Object... pathVariables) {
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity(url, requestObject, String.class, pathVariables);
        return postResponse;
    }

    public static <T> T toModel(String json, Class<T> claz) {
        try {
            T model = mapper.readValue(json, claz);
            return model;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper function for asserting that the response is an error.
     *
     * @param actual          received response as {@code String}
     * @param expectedStatus
     * @param expectedMessage
     * @param expectedPath
     * @throws Exception
     */
    public static void assertIsError(String actual,
                                     HttpStatus expectedStatus,
                                     String expectedMessage,
                                     String expectedPath) throws Exception {
        JSONObject object = getJsonObject(actual);
        assertIsError(object, expectedStatus, expectedMessage, expectedPath);
    }

    /**
     * Helper function for asserting that the response is an error.
     *
     * @param actual          received response as {@code JSONObject}
     * @param expectedStatus
     * @param expectedMessage
     * @param expectedPath
     */
    public static void assertIsError(JSONObject actual,
                                     HttpStatus expectedStatus,
                                     String expectedMessage,
                                     String expectedPath) throws Exception {
        assertThat(actual.length()).isEqualTo(5);
        assertThat(actual.getInt("status")).isEqualTo(expectedStatus.value());
        assertThat(actual.getString("message")).isEqualTo(expectedMessage);
        assertThat(actual.getString("error")).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(actual.getString("path")).isEqualTo(expectedPath);
        assertThat(actual.getString("timestamp")).isNotNull();
    }

    private static JSONObject getJsonObject(String json) {
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
