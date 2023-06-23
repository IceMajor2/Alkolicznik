package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import jakarta.annotation.Nullable;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class TestUtils {

    private static JdbcTemplate jdbcTemplate;
    private static TestRestTemplate restTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        TestUtils.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        TestUtils.restTemplate = restTemplate;
    }

    /** Acquire {@code RowMapper<Store>} that maps
     *  SQL response into a {@code Store} object.
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

    /** Acquire {@code RowMapper<Beer>} that maps
     *  SQL response into a {@code Beer} object.
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

    /**
     * Get an array of values assigned to a key in JSON body.
     * @param json JSON string
     * @param key name of JSON key
     * @return {@code JSONArray} of key's values
     */
    public static JSONArray getValues(String json, String key) {
        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray values = documentContext.read("$..%s".formatted(key));
        return values;
    }

    /**
     * Get count of key-value pairs in JSON body.
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
     * Convert list of strings into an array of strings.
     *
     * @param stringList list of strings
     * @return array of strings
     */
    public static String[] stringListToArray(List<String> stringList) {
        return stringList.toArray(new String[0]);
    }

    public static void assertCreatedBeerResponseIsCorrect(HttpStatus expectedStatus,
                                                          BeerRequestDTO request,
                                                          @Nullable BeerResponseDTO expectedResponse) {
        ResponseEntity<BeerResponseDTO> postResponse = restTemplate
                .postForEntity("/api/beer", request, BeerResponseDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(expectedStatus);
        if(expectedStatus.is4xxClientError()) {
            return;
        }
        BeerResponseDTO created = postResponse.getBody();
        URI location = postResponse.getHeaders().getLocation();

        assertThat(created.getId()).isEqualTo(expectedResponse.getId());
        assertThat(created.getFullName()).isEqualTo(expectedResponse.getFullName());
        assertThat(created.getVolume()).isEqualTo(expectedResponse.getVolume());

        // Fetch just-created entity through controller.
        ResponseEntity<BeerResponseDTO> getResponse = restTemplate
                .getForEntity(location, BeerResponseDTO.class);
        if (expectedStatus.is2xxSuccessful()) {
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        } else {
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
        BeerResponseDTO fetchController = getResponse.getBody();

        assertThat(fetchController).isEqualTo(created);

        // Additionally: fetch created entity directly from database using JDBCTemplate.
        BeerResponseDTO fetchJdbc = TestUtils.convertJdbcQueryToDto
                ("SELECT * FROM beer WHERE beer.id = %d".formatted(created.getId()),
                        TestUtils.mapToBeer());

        assertThat(created).isEqualTo(fetchJdbc);
    }
}
