package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.TestUtils;
import com.demo.alkolicznik.dto.BeerPriceRequestDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static com.demo.alkolicznik.TestUtils.*;
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
    void contextLoads() {
    }

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get store of valid id")
        public void getStoreTest() {
            var getResponse = getRequest("/api/store/{id}", 3L);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String json = getResponse.getBody();
            Store actual = toStore(json);

            Store expected = createStore(3L, "Lidl");
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Error: get store of invalid id")
        public void getStoreNotExistingTest() throws Exception {
            var getResponse = getRequest("/api/store/{id}", 9999L);

            String json = getResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of 9999 id", "/api/store/9999");
        }

        @Test
        @DisplayName("Get all stores")
        public void getStoresAllTest() {
            var getResponse = getRequest("/api/store");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String json = getResponse.getBody();
            List<Store> actual = toStoreList(json);

            assertThat(actual).isEqualTo(stores);
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("Create and get valid store")
        @DirtiesContext
        public void createAndGetStoreTest() {

        }
    }

    @Nested
    class BeerPriceRequests {

    }

    public void createAndGetStoreTest() {}

    public void addUnnamedStoreShouldReturn400Test() {}

    public void addAlreadyExistingStoreShouldReturn400Test() {}

    public void createStoreResponseBodyOKTest() {}

    public void addValidBeerToStoreTest() {}

    public void getAllStoresTest() {}
}
