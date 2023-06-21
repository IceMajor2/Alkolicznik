package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data_sql/schema.sql", "/data_sql/beer-data.sql", "/data_sql/store-data.sql", "/data_sql/store_equipment-data.sql"})
public class BeerApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Store> stores;

    private List<Beer> beers;

    // @BeforeAll does not work because it is executed
    // even before @Sql annotation
    @BeforeEach
    public void setUp() {
        this.stores = TestUtils.getStores();
        this.beers = TestUtils.getBeers();
    }

    /**
     * Launch this test to see whether the
     * ApplicationContext loads correctly.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * Get loaded at launch (through .sql script) beer.
     */
    @Test
    public void getBeerTest() {
        ResponseEntity<Beer> response = restTemplate.getForEntity("/api/beer/1", Beer.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Beer beer = response.getBody();
        Beer expected = jdbcTemplate.queryForObject("SELECT * FROM beer WHERE beer.id = 1", TestUtils.mapToBeer());

        assertThat(beer).isEqualTo(expected);
    }

    /**
     * Creating new beer should return location of the newly
     * created object within response entity. GET request to
     * this URI location should return the just-posted beer.
     */
    @Test
    @DirtiesContext
    public void createAndGetBeerTest() {
        // Create new Beer and post it to database.
        Beer beer = new Beer("Lech");
        ResponseEntity<Beer> postResponse = restTemplate.postForEntity("/api/beer", beer, Beer.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Beer savedBear = postResponse.getBody();
        URI location = postResponse.getHeaders().getLocation();

        // Fetch just-created entity from database through controller.
        ResponseEntity<Beer> getResponse = restTemplate.getForEntity(location, Beer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Beer actual = getResponse.getBody();
        assertThat(actual).isEqualTo(savedBear);

        // Additionally: fetch the beer directly from database.
        String sql = "SELECT * FROM beer WHERE beer.id = ?";
        Beer dbBeer = jdbcTemplate.queryForObject(sql, TestUtils.mapToBeer(), savedBear.getId());

        assertThat(dbBeer).isEqualTo(savedBear);
    }

    /**
     * Should return HTTP 404 (NOT_FOUND) when fetching
     * non-existent beer object from database.
     */
    @Test
    public void getNonExistingBeerShouldReturn404Test() {
        ResponseEntity<Beer> getResponse = restTemplate.getForEntity("/api/beer/9999", Beer.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * {@code POST /api/beer} - check body of 200 OK response.
     */
    @Test
    @DirtiesContext
    public void createBeerResponseBodyOKTest() throws JSONException {
        Beer beer = new Beer("Okocim");
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity("/api/beer", beer, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        JSONObject jsonObject = TestUtils.getJsonObject(postResponse.getBody());
        String nameResponse = jsonObject.getString("name");
        Long nameId = jsonObject.getLong("id");
        int lengthResponse = jsonObject.length();

        assertThat(nameResponse).isEqualTo("Okocim");
        assertThat(nameId).isEqualTo(7L);
        assertThat(lengthResponse)
                .withFailMessage("Amount of key-value pairs do not match." +
                                "\nExpected: %d" +
                                "\nActual: %d",
                        2, lengthResponse)
                .isEqualTo(2);
    }

    /**
     * {@code GET /api/beer} - request should return an array of all beers in database.
     */
    @Test
    public void getAllBeersTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/api/beer", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Compare actual and expected beer names.
        JSONArray beerNames = TestUtils.getValues(response.getBody(), "name");
        String[] beerNamesDb = TestUtils.convertNamesToArray(
                this.beers.stream().map(Beer::getName).toList());
        assertThat(beerNames).containsExactly((Object[]) beerNamesDb);

        // Compare actual and expected beer ids.
        JSONArray beerIDs = TestUtils.getValues(response.getBody(), "id");
        List<Long> longBeerIDs = this.beers.stream().map(Beer::getId).toList();
        List<Integer> intBeerIDs = TestUtils.convertLongListToIntList(longBeerIDs);
        Integer[] beerIDsDb = TestUtils.convertIdsToArray(intBeerIDs);
        assertThat(beerIDs).containsExactly((Object[]) beerIDsDb);

        int length = TestUtils.getLength(response.getBody());
        assertThat(length).isEqualTo(6);
    }
}
