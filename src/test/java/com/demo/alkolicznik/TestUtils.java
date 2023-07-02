package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

    public static StoreResponseDTO createStoreResponse(Long id, String name, String city, String street) {
        StoreResponseDTO store = new StoreResponseDTO();
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
    public static BeerResponseDTO createBeerResponse(Long id, String name, Double volume) {
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
    public static BeerRequestDTO createBeerRequest(String brand, String type, Double volume) {
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
     * @param volume
     * @param price
     * @return {@code BeerPriceRequestDTO}
     */
    public static BeerPriceRequestDTO createBeerPriceRequest(String beerName, Double volume, Double price) {
        BeerPriceRequestDTO request = new BeerPriceRequestDTO();
        request.setBeerName(beerName);
        request.setBeerVolume(volume);
        request.setPrice(price);
        return request;
    }

    /**
     * Create {@code BeerPriceResponseDTO}. Used mainly for hardcoding the expected
     * response in tests. If you don't want to specify some parameter,
     * just replace it with {@code null}.
     *
     * @param beerResponseDTO  mapped to DTO {@code Beer} model
     * @param storeResponseDTO mapped to DTO {@code Store} model
     * @param price
     * @return {@code BeerPriceResponseDTO}
     */
    public static BeerPriceResponseDTO createBeerPriceResponse(BeerResponseDTO beerResponseDTO,
                                                               StoreResponseDTO storeResponseDTO,
                                                               double price) {
        BeerPriceResponseDTO response = new BeerPriceResponseDTO();
        response.setBeer(beerResponseDTO);
        response.setStore(storeResponseDTO);
        response.setPrice(price);
        return response;
    }

    public static <T> List<T> toModelList(String json, Class<T> clazz) {
        JSONArray array = getJsonArray(json);

        List<T> tObjects = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
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
     * Helper method for sending an {@code HTTP GET}
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
     * Helper method for sending an {@code HTTP GET}
     * request through {@code TestRestTemplate} with parameters in URL.
     *
     * @param url        url of target request
     * @param parameters parameters as {@code Map}
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> getRequest(String url, Map<String, String> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        for (var entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), "{%s}".formatted(entry.getKey()));
        }
        String urlTemplate = builder.encode().toUriString();

        ResponseEntity<String> getResponse = restTemplate
                .exchange(urlTemplate, HttpMethod.GET, HttpEntity.EMPTY, String.class, parameters);
        return getResponse;
    }

    /**
     * Helper method for sending an {@code HTTP POST}
     * request through {@code TestRestTemplate}.
     *
     * @param url           url of target request
     * @param requestObject object to send to url
     * @param pathVariables url variables
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> postRequest(String url, Object requestObject, Object... pathVariables) {
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity(url, requestObject, String.class, pathVariables);
        return postResponse;
    }

    /**
     * Helper method for sending an {@code HTTP GET}
     * request through {@code TestRestTemplate} with basic auth.
     *
     * @param url      url of target request
     * @param username
     * @param password
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> getRequestWithBasicAuth(String url, String username, String password) {
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth(username, password)
                .getForEntity(url, String.class);
        return getResponse;
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
                                     String expectedPath) {
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
                                     String expectedPath) {

        try {
            assertThat(actual.getInt("status")).isEqualTo(expectedStatus.value());
        } catch (JSONException e) {
            fail("'status' key is not present");
        }
        try {
            assertThat(actual.getString("message")).isEqualTo(expectedMessage);
        } catch (JSONException e) {
            fail("'message' key is not present");
        }
        try {
            assertThat(actual.getString("error")).isEqualTo(expectedStatus.getReasonPhrase());
        } catch (JSONException e) {
            fail("'error' key is not present");
        }
        try {
            assertThat(actual.getString("path")).isEqualTo(expectedPath);
        } catch (JSONException e) {
            fail("'path' key is not present");
        }
        try {
            assertThat(actual.getString("timestamp")).isNotNull();
        } catch (JSONException e) {
            fail("'timestamp' key is not present");
        }
        assertThat(actual.length()).isEqualTo(5);
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
