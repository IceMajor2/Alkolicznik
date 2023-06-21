package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class StoreApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

    /**
     * Launch this test to see whether the
     * ApplicationContext loads correctly.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * GET '/api/store' should return a list of stores.
     */
    @Test
    public void getStoresTest() {
        ResponseEntity<List<Store>> getResponse = restTemplate
                .exchange("/api/store",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<Store>>() {
                        });
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Store> actualStores = getResponse.getBody();
        assertThat(actualStores).isEqualTo(this.stores);
    }

    /**
     * {@code POST /api/store} - valid creation should
     * return saved {@code Store} and {@code 201 CREATED}.
     * <br>
     * {@code GET /api/store/{id}} - previously acquired URI from
     * post response should return {@code Store} and {@code 200 OK}.
     */
    @Test
    @DirtiesContext
    public void createAndGetStoreTest() {
        // Create new Store and post it to database.
        Store store = new Store("Primo");
        ResponseEntity<Store> postResponse = restTemplate
                .postForEntity("/api/store", store, Store.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();
        Store savedStore = postResponse.getBody();

        // Fetch just-created object from database.
        ResponseEntity<Store> getResponse = restTemplate
                .getForEntity(location, Store.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Store actual = getResponse.getBody();
        assertThat(actual).isEqualTo(savedStore);

        // Additionally: fetch the store directly from database.
        String sql = "SELECT * FROM store WHERE store.id = ?";
        Store storeDb = jdbcTemplate.queryForObject(sql, TestUtils.mapToStore(), savedStore.getId());

        assertThat(storeDb).isEqualTo(savedStore);
    }

    /**
     * {@code POST /api/store} with empty name should return {@code 400 Bad Request}.
     */
    @Test
    @DirtiesContext
    public void addUnnamedStoreShouldReturn400Test() {
        Store store = new Store("");
        ResponseEntity<Store> postResponse = restTemplate
                .postForEntity("/api/store", store, Store.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        store = new Store();
        postResponse = restTemplate.postForEntity("/api/store", store, Store.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        store = new Store("   \t ");
        postResponse = restTemplate.postForEntity("/api/store", store, Store.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * {@code POST /api/store} with already existing store should return {@code 400 Bad Request}.
     */
    @Test
    @DirtiesContext
    public void addAlreadyExistingStoreShouldReturn400Test() {
        Store store = new Store("Biedronka");
        ResponseEntity<Store> postResponse = restTemplate
                .postForEntity("/api/store", store, Store.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    /**
     * {@code POST /api/store} - check body of 201 OK response.
     */
    public void createStoreResponseBodyOKTest() throws JSONException {
        Store store = new Store("Zoltek");
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity("/api/store", store, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        JSONObject jsonObject = TestUtils.getJsonObject(postResponse.getBody());
        String nameResponse = jsonObject.getString("name");
        Long nameId = jsonObject.getLong("id");
        int lengthResponse = jsonObject.length();

        assertThat(nameResponse).isEqualTo("Zoltek");
        assertThat(nameId).isEqualTo(7L);
        assertThat(lengthResponse)
                .withFailMessage("Amount of key-value pairs do not match." +
                                "\nExpected: %d" +
                                "\nActual: %d",
                        2, lengthResponse)
                .isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void addValidBeerToStoreTest() throws JSONException {
        BeerPriceRequestDTO beerPriceRequestDTO = new BeerPriceRequestDTO();
        beerPriceRequestDTO.setBeer("Tyskie");
        beerPriceRequestDTO.setPrice(3.29);

        ResponseEntity<String> response = restTemplate
                .postForEntity("/api/store/4/beer", beerPriceRequestDTO, String.class);

        JSONObject jsonObject = TestUtils.getJsonObject(response.getBody());
        Long storeId = jsonObject.getLong("store_id");
        String storeName = jsonObject.getString("store_name");
        Long beerId = jsonObject.getLong("beer_id");
        String beerName = jsonObject.getString("beer_name");
        double price = jsonObject.getDouble("price");

        assertThat(storeId).isEqualTo(4L);
        assertThat(storeName).isEqualTo("ABC");
        assertThat(beerId).isEqualTo(3L);
        assertThat(beerName).isEqualTo("Tyskie");
        assertThat(price).isEqualTo(3.29);
    }

    /**
     * {@code GET /api/store} - request should return an array of all stores in database.
     */
    @Test
    public void getAllStoresTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/api/store", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Compare actual and expected store names.
        JSONArray storeNames = TestUtils.getValues(response.getBody(), "name");
        String[] storeNamesDb = TestUtils.convertNamesToArray(
                this.stores.stream().map(Store::getName).toList());
        assertThat(storeNames).containsExactly((Object[]) storeNamesDb);

        // Compare actual and expected store ids.
        JSONArray storeIDs = TestUtils.getValues(response.getBody(), "id");
        List<Long> longStoreIDs = this.stores.stream().map(Store::getId).toList();
        List<Integer> intStoreIDs = TestUtils.convertLongListToIntList(longStoreIDs);
        Integer[] storeIDsDb = TestUtils.convertIdsToArray(intStoreIDs);
        assertThat(storeIDs).containsExactly((Object[]) storeIDsDb);

        int length = TestUtils.getLength(response.getBody());
        assertThat(length).isEqualTo(6);
    }
}
