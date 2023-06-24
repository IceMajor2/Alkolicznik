package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class TestUtils {

    private static JdbcTemplate jdbcTemplate;
    private static TestRestTemplate restTemplate;
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        TestUtils.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        TestUtils.restTemplate = restTemplate;
    }

    /**
     * Acquire {@code RowMapper<Store>} that maps
     * SQL response into a {@code Store} object.
     *
     * @return {@code RowMapper<Store>}
     */
    public static RowMapper<Store> mapToStore() {
        return new RowMapper<Store>() {
            @Override
            public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
                Store store = new Store();
                store.setId(rs.getLong("id"));
                store.setName(rs.getString("name"));
                return store;
            }
        };
    }

    /**
     * Acquire {@code RowMapper<Beer>} that maps
     * SQL response into a {@code Beer} object.
     *
     * @return {@code RowMapper<Beer>}
     */
    public static RowMapper<Beer> mapToBeer() {
        return new RowMapper<Beer>() {
            @Override
            public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Beer beer = new Beer();
                beer.setId(rs.getLong("id"));
                beer.setBrand(rs.getString("brand"));
                beer.setType(rs.getString("type"));
                beer.setVolume(rs.getDouble("volume"));
                return beer;
            }
        };
    }

    /**
     * Converts {@code Beer} original object into a used-by-controller DTO.
     *
     * @param query      SQL-native query
     * @param beerMapper {@code RowMapper} that maps SQL response into {@code Beer}
     * @return {@code BeerResponseDTO}
     */
    public static BeerResponseDTO convertJdbcQueryToDto(String query, RowMapper<Beer> beerMapper) {
        return new BeerResponseDTO(jdbcTemplate.queryForObject(query, beerMapper));
    }

    /**
     * Convert JSON in {@code String} to {@code JSONObject}.
     *
     * @param json JSON string
     * @return {@code JSONObject}
     */
    public static JSONObject getJsonObject(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<BeerResponseDTO> convertJsonArrayToList(String json) {
        JSONArray array = getJsonArray(json);

        List<BeerResponseDTO> responseDTOs = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                String beerAsString = array.getString(i);
                BeerResponseDTO responseDTO = toDTO(beerAsString);
                responseDTOs.add(responseDTO);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseDTOs;
    }

    public static List<BeerResponseDTO> listToDTOList(List<Beer> beers) {
        List<BeerResponseDTO> dtos = beers.stream()
                .map(BeerResponseDTO::new)
                .collect(Collectors.toList());
        return dtos;
    }

    public static JSONArray getJsonArray(String json) {
        try {
            JSONArray array = new JSONArray(json);
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get count of key-value pairs in JSON body.
     *
     * @param json JSON string
     * @return key-value pairs (as {@code int})
     */
    public static int getLength(String json) {
        DocumentContext documentContext = JsonPath.parse(json);
        return documentContext.read("$.length()");
    }

    /**
     * Convert list of integers into an array of integers.
     *
     * @param intList list of integers
     * @return array of integers
     */
    public static Integer[] intListToArray(List<Integer> intList) {
        return intList.toArray(new Integer[0]);
    }

    /**
     * Convert list of {@code Long} values to list of integers.
     *
     * @param longList {@code List<Long>}
     * @return {@code List<Integer>}
     */
    public static List<Integer> longListToIntList(List<Long> longList) {
        return longList.stream()
                .map(num -> (Integer) num.intValue())
                .toList();
    }

    /**
     * Make {@code GET} request for beer by id.
     *
     * @param id beer id
     * @return {@code ResponseEntity<String>}
     */
    public static ResponseEntity<String> requestBeer(Long id) {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/api/beer/{id}", String.class, id);
        return getResponse;
    }

    public static ResponseEntity<String> requestAllBeers() {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/api/beer", String.class);
        return getResponse;
    }

    /**
     * Converts JSON body to {@code BeerResponseDTO} object.
     *
     * @param json JSON representing {@code BeerResponseDTO}
     * @return {@code BeerResponseDTO} object
     */
    public static BeerResponseDTO toDTO(String json) {
        try {
            BeerResponseDTO dto = mapper.readValue(json, BeerResponseDTO.class);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Provided JSON does not represent BeerResponseDTO object");
        }
    }

    /**
     * Make {@code POST} request to save beer.
     *
     * @param request {@code BeerRequestDTO} object as request entity
     * @return {@code ResponseEntity<String>}
     */
    public static ResponseEntity<String> postBeer(BeerRequestDTO request) {
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity("/api/beer", request, String.class);
        return postResponse;
    }

    /**
     * Helper function for asserting a response is an error.
     *
     * @param actual          tested response as {@code JSONObject} object
     * @param expectedStatus  expected {@code HttpStatus}
     * @param expectedMessage
     * @param expectedPath
     */
    public static void assertIsError(JSONObject actual,
                                     HttpStatus expectedStatus,
                                     String expectedMessage,
                                     String expectedPath) {
        try {
            assertThat(actual.length()).isEqualTo(5);
            assertThat(actual.getString("timestamp")).isNotNull();
            assertThat(actual.getInt("status")).isEqualTo(expectedStatus.value());
            assertThat(actual.getString("message")).isEqualTo(expectedMessage);
            assertThat(actual.getString("error")).isEqualTo(expectedStatus.getReasonPhrase());
            assertThat(actual.getString("path")).isEqualTo(expectedPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method for creating a {@code BeerRequestDTO}. If you don't
     * want to specify some parameter, just replace it with {@code null}.
     *
     * @param brand
     * @param type
     * @param volume
     * @return {@code BeerRequestDTO}
     */
    public static BeerRequestDTO createBeerRequest(String brand, String type, Double volume) {
        BeerRequestDTO request = new BeerRequestDTO();
        if (brand != null) {
            request.setBrand(brand);
        }
        if (type != null) {
            request.setType(type);
        }
        if (volume != null) {
            request.setVolume(volume);
        }
        return request;
    }

    /**
     * Convert list of strings into an array of strings.
     *
     * @param stringList list of strings
     * @return array of strings
     */
    public static String[] stringListToArray(List<String> stringList) {
        return stringList.toArray(new String[0]);
    }
}
