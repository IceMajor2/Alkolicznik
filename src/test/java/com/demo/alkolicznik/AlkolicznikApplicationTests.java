package com.demo.alkolicznik;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/data_sql/schema.sql", "/data_sql/beer-data.sql", "/data_sql/store-data.sql"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlkolicznikApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private List<Store> stores;

    private List<Beer> beers;

    private static boolean initialized = false;
    // @BeforeAll does not work because it is executed
    // even before @Sql annotation
    @BeforeEach
    public void setUp() {
        if(initialized) {
            return;
        }
        this.stores = this.getStores();
        this.beers = this.getBeers();
        initialized = true;
    }

    /**
     * Launch this test to see whether the
     * ApplicationContext loads correctly.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * Index.html is expected to
     * be accessible by anyone.
     */
    @Test
    public void anyoneCanAccessIndexPageTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Get loaded at launch (through .sql script) beer.
     */
    @Test
    public void getBeerTest() {
        ResponseEntity<Beer> response = restTemplate.getForEntity("/api/beer/1", Beer.class);
        this.getBeers();
        this.getStores();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Beer beer = response.getBody();
        Beer expected = jdbcTemplate.queryForObject("SELECT * FROM beer WHERE beer.id = 1", mapToBeer());

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
        Beer dbBeer = jdbcTemplate.queryForObject(sql, mapToBeer(), savedBear.getId());

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
        assertThat(actualStores).isEqualTo(this.getStores());
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
        System.out.println(location);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Store actual = getResponse.getBody();
        assertThat(actual).isEqualTo(savedStore);

        // Additionally: fetch the store directly from database.
        String sql = "SELECT * FROM store WHERE store.id = ?";
        Store storeDb = jdbcTemplate.queryForObject(sql, mapToStore(), savedStore.getId());

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

    private List<Store> getStores() {
        String sql = "SELECT * FROM store";
        List<Store> initializedStores = jdbcTemplate.query(sql, mapToStore());
        return initializedStores;
    }

    private List<Beer> getBeers() {
        String sql = "SELECT * FROM beer";
        List<Beer> initializedBeers = jdbcTemplate.query(sql, mapToBeer());
        return initializedBeers;
    }

    private RowMapper<Store> mapToStore() {
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

    private RowMapper<Beer> mapToBeer() {
        return new RowMapper<Beer>() {
            @Override
            public Beer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Beer beer = new Beer();
                beer.setId(rs.getLong("id"));
                beer.setName(rs.getString("name"));
                return beer;
            }
        };
    }
}
