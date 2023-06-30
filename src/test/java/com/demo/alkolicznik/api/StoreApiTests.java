package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static com.demo.alkolicznik.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class StoreApiTests {

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

            String actualJson = getResponse.getBody();
            Store actual = toModel(actualJson, Store.class);

            Store expected = createStoreResponse(3L, "Lidl", "Olsztyn", "ul. Iwaszkiewicza 1");
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
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
        public void createStoreTest() {
            var postResponse = postRequest("/api/store",
                    createStoreRequest("Dwojka", "Krakow", "ul. Powstancow 9"));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            Store actual = toModel(actualJson, Store.class);

            Store expected = createStoreResponse(7L, "Dwojka", "Krakow", "ul. Powstancow 9");
            String expectedJson = toJsonString(expected);

            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create invalid store: NAME null")
        @DirtiesContext
        public void createStoreNameNullTest() throws Exception {
            var postResponse = postRequest("/api/store", createStoreRequest(null, "Mragowo", "ul. Wyspianskiego 17"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/beer");
        }

        @Test
        @DisplayName("Create invalid store: NAME blank & NAME empty")
        @DirtiesContext
        public void createStoreNameBlankAndEmptyTest() throws Exception {
            var postResponse = postRequest("/api/store", createStoreRequest("", "Gdansk", "ul. Hallera 120"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/beer");

            postResponse = postRequest("/api/store", createStoreRequest("\t \t   \n", "Sopot", "ul. Olsztynska 1"));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/beer");
        }

        @Test
        @DisplayName("Create invalid store: CITY null")
        @DirtiesContext
        public void createStoreCityNullTest() throws Exception {
            var postResponse = postRequest("/api/store",
                    createStoreRequest("Lubi", null, "ul. Kwiatkowa 3"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
        }

        @Test
        @DisplayName("Create invalid store: CITY blank & empty")
        @DirtiesContext
        public void createStoreCityBlankAndEmptyTest() throws Exception {
            var postResponse = postRequest("/api/store",
                    createStoreRequest("Lubi", "", "ul. Kwiatkowa 3"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");

            postResponse = postRequest("/api/store",
                    createStoreRequest("Lubi", "\t \n", "ul. Brzozowa 31"));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
        }

        @Test
        @DisplayName("Create invalid store: STREET null")
        @DirtiesContext
        public void createStoreStreetNullTest() throws Exception {
            var postResponse = postRequest("/api/store",
                    createStoreRequest("Primo", "Olsztyn", null));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
        }

        @Test
        @DisplayName("Create invalid store: STREET blank & empty")
        @DirtiesContext
        public void createStoreStreetBlankAndEmptyTest() throws Exception {
            var postResponse = postRequest("/api/store",
                    createStoreRequest("Primo", "Olsztyn", ""));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");

            postResponse = postRequest("/api/store",
                    createStoreRequest("Primo", "Olsztyn", "\t     "));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
        }

        @Test
        @DisplayName("Create invalid store: ALREADY_EXISTS")
        @DirtiesContext
        public void createStoreAlreadyExistsTest() throws Exception {
            var postResponse = postRequest("/api/store", createStoreRequest("Lidl", "Szczecin", "ul. Poranna 32"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Store already exists", "/api/store");
        }

        @Test
        @DisplayName("Create invalid store: NAME blank, CITY null, STREET empty")
        @DirtiesContext
        public void createStoreNameBlankCityNullStreetEmptyTest() throws Exception {
            var postResponse = postRequest("/api/store",
                    createStoreRequest(" \t", null, ""));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified;" +
                    " City was not specified; Street was not specified", "/api/store");
        }
    }

    @Nested
    class BeerPriceRequests {

        @Test
        @DisplayName("Valid add beer to store")
        @DirtiesContext
        public void addBeerToStoreTest() {
            var postResponse = postRequest("/api/store/{id}/beer",
                    createBeerPriceRequest("Perla Chmielowa Pils", 3.69),
                    2);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();

            BeerPriceResponseDTO expected = createBeerPriceResponse(2L, "Biedronka", 1L, "Perla Chmielowa Pils", 3.69);
            String expectedJson = toJsonString(expected);

            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }
}
