package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
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

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
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
            assertThat(actual).hasSameElementsAs(expected);
        }

        @Test
        @DisplayName("Get beer prices of beer")
        public void getBeerPricesOfBeerTest() {
            var getResponse = getRequest("/api/beer/3/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Beer beer = getBeer(3L, beers);

            List<BeerPriceResponseDTO> expected = beer.getPrices()
                    .stream()
                    .map(BeerPriceResponseDTO::new)
                    .toList();
            assertThat(actual).hasSameElementsAs(expected);
        }

        @Test
        @DisplayName("Get beer prices of beer: BEER_NOT_EXISTS")
        public void getBeerPricesOfBeerNotExistsTest() {
            var getResponse = getRequest("/api/beer/333/beer-price");

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '333' id",
                    "/api/beer/333/beer-price");
        }

        @Test
        @DisplayName("Get beer price")
        public void getBeerPriceTest() {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", 3L, "beer_id", 3L));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(3L, beers)),
                    createStoreResponse(getStore(3L, stores)),
                    "PLN 4.19"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer price of defined beer in a city")
        public void getBeersPriceInCityTest() {
            var getResponse = getRequest("/api/beer/2/beer-price", Map.of("city", "Warszawa"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Beer expectedBeer = getBeer(2L, beers);
            List<BeerPriceResponseDTO> expected = new ArrayList<>();
            for (BeerPrice beerPrice : expectedBeer.getPrices()) {
                if (beerPrice.getStore().getCity().equals("Warszawa")) {
                    expected.add(new BeerPriceResponseDTO(beerPrice));
                }
            }
            assertThat(actual).hasSameElementsAs(expected);
        }

        @Test
        @DisplayName("Get invalid beer price of defined beer in a city: BEER_NOT_EXISTS")
        public void getBeersPriceInCityBeerNotExistsTest() {
            var getResponse = getRequest("/api/beer/10/beer-price", Map.of("city", "Gdansk"));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '10' id",
                    "/api/beer/10/beer-price");
        }

        @Test
        @DisplayName("Get invalid beer price of defined beer in a city: CITY_NOT_EXISTS")
        public void getBeersPriceInCityCityNotExistsTest() {
            var getResponse = getRequest("/api/beer/5/beer-price", Map.of("city", "Ciechocinek"));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: 'Ciechocinek'",
                    "/api/beer/5/beer-price");
        }

        @Test
        @DisplayName("Get valid beer price of defined beer in a city: city EXISTS but EMPTY")
        public void getBeersPriceInCityCityEmptyTest() {
            var getResponse = getRequest("/api/beer/4/beer-price", Map.of("city", "Gdansk"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();

            String expectedJson = "[]";

            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Get beer price: STORE_NOT_EXISTS")
        public void getBeerPriceStoreNotExistsTest() {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", 95L, "beer_id", 3L));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '95' id",
                    "/api/beer-price");
        }

        @Test
        @DisplayName("Get beer price: BEER_NOT_EXISTS")
        public void getBeerPriceBeerNotExistsTest() {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", 5L, "beer_id", 35L));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '35' id",
                    "/api/beer-price");
        }

        @Test
        @DisplayName("Get beer price: BEER_PRICE_NOT_EXISTS")
        public void getBeerPriceNotExistsTest() {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", 3L, "beer_id", 4L));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Store does not currently sell this beer",
                    "/api/beer-price");
        }

        @Test
        @DisplayName("Get beer prices from store")
        public void getBeerPricesFromStoreTest() {
            var getResponse = getRequest("/api/store/3/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Store store = getStore(3L, stores);
            List<BeerPriceResponseDTO> expected = store.getPrices().stream()
                    .map(BeerPriceResponseDTO::new)
                    .toList();
            assertThat(actual).hasSameElementsAs(expected);
        }

        @Test
        @DisplayName("Get beer prices from non-existing store")
        public void getBeerPricesFromStoreNotExistsTest() {
            var getResponse = getRequest("/api/store/8/beer-price");

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '8' id",
                    "/api/store/8/beer-price");
        }

        @Test
        @DisplayName("Get beer prices of empty store")
        public void getBeerPricesFromStoreEmptyTest() {
            var getResponse = getRequest("/api/store/7/beer-price");
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
                    "No such city: 'Bydgoszcz'",
                    "/api/beer-price");
        }
    }

    @Nested
    class PostRequestsParam {

        @Test
        @DisplayName("Add valid beer price (ID) to store")
        @DirtiesContext
        public void addBeerPriceIdTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    Map.of("beer_id", 3L, "beer_price", 4.19));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(3L, beers)),
                    createStoreResponse(getStore(1L, stores)),
                    "PLN 4.19"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", 3L, "store_id", 1L));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: STORE_NOT_EXISTS")
        public void addBeerPriceIdStoreNotExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/9999/beer-price",
                    Map.of("beer_id", 3L, "beer_price", 4.19));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '9999' id",
                    "/api/store/9999/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: PRICE negative and zero")
        public void addBeerPriceIdVolumeNegativeAndZeroTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    Map.of("beer_id", 5L, "beer_price", 0d));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    Map.of("beer_id", 5L, "beer_price", -5.213));

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: BEER_NOT_EXISTS")
        public void addBeerPriceIdBeerNotExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    Map.of("beer_id", 999L, "beer_price", 6.69));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '999' id",
                    "/api/store/1/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price (ID) to store: BEER_PRICE_ALREADY_EXISTS")
        public void addBeerPriceIdAlreadyExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    Map.of("beer_id", 2, "beer_price", 6.69));

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
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Perla Chmielowa Pils", 0.5, 3.69));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(1L, beers)),
                    createStoreResponse(getStore(2L, stores)),
                    "PLN 3.69"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", 1L, "store_id", 2L));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add valid beer price to store: ALL (1-piece name)")
        @DirtiesContext
        public void addBeerPriceToStoreTest2() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    createBeerPriceRequest("Zubr", 0.5, 2.79));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(4L, beers)),
                    createStoreResponse(getStore(1L, stores)),
                    "PLN 2.79"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", 4L, "store_id", 1L));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add valid beer price to store: VOLUME not specified")
        @DirtiesContext
        public void addBeerPriceToStoreDefaultVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/7/beer-price",
                    createBeerPriceRequest("Perla Chmielowa Pils", null, 3.69));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(1L, beers)),
                    createStoreResponse(getStore(7L, stores)),
                    "PLN 3.69");
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", 1L, "store_id", 7L));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_PRICE_ALREADY_EXISTS")
        public void addBeerPriceAlreadyExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    createBeerPriceRequest("Komes Porter Malinowy", 0.33, 8.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer is already in store",
                    "/api/store/1/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_NOT_EXISTS (different volume)")
        public void createBeerPriceBeerExistsButDifferentVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Zubr", 0.6, 3.19));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer 'Zubr' of '%.2f' volume".formatted(0.6),
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: VOLUME negative and equal to zero")
        public void createBeerPriceNegativeAndZeroVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/6/beer-price",
                    createBeerPriceRequest("Tyskie Gronie", -1.0, 3.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/store/6/beer-price");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store/6/beer-price",
                    createBeerPriceRequest("Tyskie Gronie", 0d, 3.09));

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/store/6/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND null")
        public void createBeerPriceBrandNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/5/beer-price",
                    createBeerPriceRequest(null, 0.5, 3.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/5/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND blank and empty")
        public void createBeerPriceBrandBlankAndEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/5/beer-price",
                    createBeerPriceRequest("", 0.5, 3.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/5/beer-price");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store/3/beer-price",
                    createBeerPriceRequest(" \t \n\n \t", 1d, 7.99));

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified",
                    "/api/store/3/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: PRICE null")
        public void createBeerPriceNegativeAndZeroPriceTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, -1d));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, 0d));

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BRAND null, VOLUME zero, PRICE negative")
        public void createBeerPricePriceNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest(null, 0d, -9.4));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer was not specified; Price must be a positive number; Volume must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("Add invalid beer price to store: BEER_NOT_EXISTS")
        public void createBeerPriceBeerNotExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/4/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, 7.99));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of 'Kormoran Miodne' name",
                    "/api/store/4/beer-price");
        }

        @Test
        @DisplayName("Add valid beer price to invalid store: STORE_NOT_EXISTS")
        @DirtiesContext
        public void createBeerPriceStoresNotExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/9999/beer-price",
                    createBeerPriceRequest("Ksiazece Zlote pszeniczne", 0.5, 3.79));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '9999' id",
                    "/api/store/9999/beer-price");
        }
    }
}
