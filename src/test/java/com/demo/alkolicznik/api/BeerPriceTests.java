package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.TestUtils.*;
import static com.demo.alkolicznik.TestUtils.toJsonString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerPriceTests {

    @Autowired
    private List<Store> stores;

    @Autowired
    private List<Beer> beers;

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get beer prices of city")
        public void getBeerPricesFromCityTest() {
            var getResponse = getRequest("/api/beer-price", Map.of("city", "Olsztyn"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            List<Store> olsztynStores = stores.stream()
                    .filter(store -> store.getCity().equals("Olsztyn"))
                    .collect(Collectors.toList());

            List<BeerPriceResponseDTO> expected = new ArrayList<>();
            for (Store store : olsztynStores) {
                for (BeerPrice beer : store.getPrices()) {
                    expected.add(new BeerPriceResponseDTO(beer));
                }
            }
            String expectedJson = toJsonString(expected);
            assertThat(actual.toArray()).containsExactlyInAnyOrder(expected.toArray());
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer prices of beer")
        public void getBeerPricesOfBeerTest() {
            var getResponse = getRequest("/api/beer/{id}/beer-price", 3L);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Beer beer = null;
            for(Beer beerObj : beers) {
                if(beerObj.getId() == 3L) {
                    beer = beerObj;
                    break;
                }
            }

            List<BeerPriceResponseDTO> expected = beer.getPrices()
                    .stream()
                    .map(BeerPriceResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);
            assertThat(actual.toArray()).containsExactlyInAnyOrder(expected.toArray());
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer prices of beer: BEER_NOT_EXISTS")
        public void getBeerPricesOfBeerNotExistsTest() {
            var getResponse = getRequest("/api/beer/{id}/beer-price", 333L);

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '333' id",
                    "/api/beer/333/beer-price");
        }

        @Test
        @DisplayName("Get beer prices from store")
        public void getBeerPricesFromStoreTest() {
            var getResponse = getRequest("/api/store/{id}/beer-price", 3L);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Store store = null;
            for (Store storeObj : stores) {
                if (storeObj.getId() == 3L) {
                    store = storeObj;
                    break;
                }
            }
            List<BeerPriceResponseDTO> expected = store.getPrices().stream()
                    .map(BeerPriceResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);
            assertThat(actual.toArray()).isEqualTo(expected.toArray());
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer prices from non-existing store")
        public void getBeerPricesFromStoreNotExistsTest() {
            var getResponse = getRequest("/api/store/{id}/beer-price", 8L);

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '8' id",
                    "/api/store/8/beer-price");
        }

        @Test
        @DisplayName("Get beer prices of empty store")
        public void getBeerPricesFromStoreEmptyTest() {
            var getResponse = getRequest("/api/store/{id}/beer-price", 7);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();

            String expectedJson = "[]";
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer prices of empty city")
        public void getBeerPricesFromCityEmptyTest() {
            var getResponse = getRequest("/api/beer-price", Map.of("city", "Gdansk"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();

            String expectedJson = "[]";
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer prices of non-existing city")
        public void getBeerPricesFromCityNotExistsTest() {
            var getResponse = getRequest("/api/beer-price", Map.of("city", "Bydgoszcz"));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city",
                    "/api/beer-price");
        }
    }

    @Nested
    class PostRequestsParam {

        @Test
        @DisplayName("Add valid beer price (ID) to store")
        @DirtiesContext
        public void addBeerPriceIdTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 3L, "beer_price", 4.19),
                    1L);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(3L, "Tyskie Gronie", 0.6),
                    createStoreResponse(1L, "Carrefour", "Olsztyn", "ul. Barcza 4"),
                    4.19
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: STORE_NOT_EXISTS")
        public void addBeerPriceIdStoreNotExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 3L, "beer_price", 4.19),
                    9999L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '9999' id",
                    "/api/store/9999/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: PRICE negative and zero")
        public void addBeerPriceIdVolumeNegativeAndZeroTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 5L, "beer_price", 0d),
                    2L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");

            postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 5L, "beer_price", -5.213),
                    2L);

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: BEER_NOT_EXISTS")
        public void addBeerPriceIdBeerNotExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 999L, "beer_price", 6.69),
                    1L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '999' id",
                    "/api/store/1/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: BEER_PRICE_ALREADY_EXISTS")
        public void addBeerPriceIdAlreadyExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    Map.of("beer_id", 2, "beer_price", 6.69),
                    1L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer is already in store",
                    "/api/store/1/beer-price");
        }
    }

    @Nested
    class PostRequestsObject {

        @Test
        @DisplayName("Add valid beer price to store: ALL")
        @DirtiesContext
        public void addBeerPriceToStoreTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Perla Chmielowa Pils", 0.5, 3.69),
                    2);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(1L, "Perla Chmielowa Pils", 0.5),
                    createStoreResponse(2L, "Biedronka", "Olsztyn", "ul. Sikorskiego-Wilczynskiego 12"), 3.69
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add valid beer price to store: ALL (1-piece name)")
        @DirtiesContext
        public void addBeerPriceToStoreTest2() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Zubr", 0.5, 2.79),
                    1L);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(4L, "Zubr", 0.5),
                    createStoreResponse(1L, "Carrefour", "Olsztyn", "ul. Barcza 4"),
                    2.79
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add valid beer price to store: VOLUME not specified")
        @DirtiesContext
        public void addBeerPriceToStoreDefaultVolumeTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Perla Chmielowa Pils", null, 3.69),
                    7);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(1L, "Perla Chmielowa Pils", 0.5),
                    createStoreResponse(7L, "Tesco", "Gdansk", "ul. Morska 22"),
                    3.69);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_PRICE_ALREADY_EXISTS")
        public void addBeerPriceAlreadyExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Komes Malinowe", 0.33, 8.09),
                    1L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer is already in store",
                    "/api/store/1/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_NOT_EXISTS (different volume)")
        public void createBeerPriceBeerExistsButDifferentVolumeTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Zubr", 0.6, 3.19),
                    2L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%.2f' volume".formatted(0.6),
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: VOLUME negative and equal to zero")
        public void createBeerPriceNegativeAndZeroVolumeTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Tyskie Gronie", -1.0, 3.09),
                    6L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/store/6/beer-price");

            postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Tyskie Gronie", 0d, 3.09),
                    6L);

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/store/6/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND null")
        public void createBeerPriceBrandNullTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest(null, 0.5, 3.09),
                    5L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/5/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND blank and empty")
        public void createBeerPriceBrandBlankAndEmptyTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("", 0.5, 3.09),
                    5L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/5/beer-price");

            postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest(" \t \n\n \t", 1d, 7.99),
                    3L);

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/3/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: PRICE null")
        public void createBeerPriceNegativeAndZeroPriceTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, -1d),
                    2L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");

            postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, 0d),
                    2L);

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND null, VOLUME zero, PRICE negative")
        public void createBeerPricePriceNullTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest(null, 0d, -9.4),
                    2L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified; Price must be a positive number; Volume must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_NOT_EXISTS")
        public void createBeerPriceBeerNotExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, 7.99),
                    4L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of 'Kormoran Miodne' name",
                    "/api/store/4/beer-price");
        }

        @Test
        @DisplayName("Add valid beer price to invalid store: STORE_NOT_EXISTS")
        public void createBeerPriceStoresNotExistsTest() {
            var postResponse = postRequest("/api/store/{id}/beer-price",
                    createBeerPriceRequest("Ksiazece Zlote pszeniczne", 0.5, 3.79),
                    9999L);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '9999' id",
                    "/api/store/9999/beer-price");
        }
    }
}
