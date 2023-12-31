package com.demo.alkolicznik.api;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.utils.matchers.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.FindingUtils.*;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "no-security", "no-vaadin"})
@TestClassOrder(ClassOrderer.Random.class)
public class BeerPriceTest {

    private final String EXPECTED_CURRENCY_UNIT;

    @Autowired
    public BeerPriceTest(@Value("${money.currency.unit:PLN}") String EXPECTED_CURRENCY_UNIT) {
        this.EXPECTED_CURRENCY_UNIT = EXPECTED_CURRENCY_UNIT;
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    class GetRequests {

        private List<Beer> beers;

        private List<Store> stores;

        @Autowired
        public GetRequests(List<Beer> beers, List<Store> stores) {
            this.beers = beers;
            this.stores = stores;
        }

        @ParameterizedTest
        @CsvSource({
                "2, 4, 2.89",
                "4, 1, 4.09",
                "5, 6, 6.09"
        })
        @DisplayName("GET: '/api/beer-price'")
        public void getBeerPriceTest(Long storeId, Long beerId, Double price) {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", storeId, "beer_id", beerId));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(beerId, beers)),
                    createStoreResponse(getStore(storeId, stores)),
                    EXPECTED_CURRENCY_UNIT + " " + price
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -5, 912})
        @DisplayName("GET: '/api/beer-price' [BEER_NOT_FOUND]")
        public void getBeerPriceBeerNotExistsTest(Long beerId) {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", 5L, "beer_id", beerId));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + beerId + "' id",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-2111, 0, 91234})
        @DisplayName("GET: '/api/beer/beer-price' [STORE_NOT_FOUND]")
        public void getBeerPriceStoreNotExistsTest(Long storeId) {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", storeId, "beer_id", 3L));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '" + storeId + "' id",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "1, 4",
                "3, 2",
                "7, 1",
                "6, 7",
                "9, 9"
        })
        @DisplayName("GET: '/api/beer-price' [NO_PRICE]")
        public void getBeerPriceNotExistsTest(Long storeId, Long beerId) {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", storeId, "beer_id", beerId));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Store does not currently sell this beer",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "0, 111",
                "5423, 919",
                "-318, 546238"
        })
        @DisplayName("GET: '/api/beer-price' [STORE_n_BEER_NOT_FOUND]")
        public void getBeerPriceBeerAndStoreNotExistsTest(Long storeId, Long beerId) {
            var getResponse = getRequest("/api/beer-price",
                    Map.of("store_id", storeId, "beer_id", beerId));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + beerId + "' id; Unable to find store of '" + storeId + "' id",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Olsztyn", "Warszawa"})
        @DisplayName("GET: '/api/beer-price?city' ordered")
        public void getBeerPricesFromCityTest(String city) {
            var getResponse = getRequest("/api/beer-price", Map.of("city", city));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            List<Store> cityStores = stores.stream()
                    .filter(store -> store.getCity().equals(city))
                    .collect(Collectors.toList());

            List<BeerPriceResponseDTO> expected = new ArrayList<>();
            for (Store store : cityStores) {
                for (BeerPrice beer : store.getPrices()) {
                    expected.add(new BeerPriceResponseDTO(beer));
                }
            }
            sortByBeerIdPriceAndStoreId(expected);
            assertThat(actual).containsExactlyElementsOf(expected);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Ilawa", "Gdansk"})
        @DisplayName("GET: '/api/beer-price?city' of empty city")
        public void getBeerPricesFromCityEmptyTest(String city) {
            // when
            var getResponse = getRequest("/api/beer-price", Map.of("city", city));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();

            // then
            String expectedJson = "[]";
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(strings = {"abdf", "skjo", ""})
        @DisplayName("GET: '/api/beer-price?city' [CITY_NOT_FOUND]")
        public void getBeerPricesFromCityNotExistsTest(String city) {
            var getResponse = getRequest("/api/beer-price", Map.of("city", city));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: '" + city + "'",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {5, 1, 6})
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' ordered")
        public void getBeerPricesOfBeerTest(Long beerId) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Beer beer = getBeer(beerId.longValue(), beers);
            List<BeerPriceResponseDTO> expected = beer.getPrices()
                    .stream()
                    .map(BeerPriceResponseDTO::new)
                    .collect(Collectors.toList());
            sortByCityPriceAndStoreId(expected);
            assertThat(actual).containsExactlyElementsOf(expected);
        }

        @ParameterizedTest
        @ValueSource(longs = {-532, 0, 5328})
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' [BEER_NOT_FOUND]")
        public void getBeerPricesOfBeerNotExistsTest(Long beerId) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + beerId + "' id",
                    "/api/beer/" + beerId + "/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {7, 8})
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' beer not sold")
        public void getBeerPricesOfBeerNotSoldTest(Long beerId) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            assertThat(jsonResponse).isEqualTo("[]");
        }

        @ParameterizedTest
        @CsvSource({
                "4, Warszawa",
                "1, Olsztyn",
                "2, Olsztyn"
        })
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price?city' ordered")
        public void getBeersPriceInCityTest(Long beerId, String city) {
            // when
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price",
                    Map.of("city", city));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            // then
            Beer expectedBeer = getBeer(beerId.longValue(), beers);
            List<BeerPriceResponseDTO> expected = new ArrayList<>();
            for (BeerPrice beerPrice : expectedBeer.getPrices()) {
                if (beerPrice.getStore().getCity().equals(city)) {
                    expected.add(new BeerPriceResponseDTO(beerPrice));
                }
            }
            sortByPriceAndStoreId(expected);
            assertThat(actual).containsExactlyElementsOf(expected);
        }

        @ParameterizedTest
        @CsvSource({
                "1, Gdansk",
                "5, Warszawa",
                "7, Olsztyn"
        })
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price?city' not selling")
        public void getBeersPriceInCityCityEmptyTest(Long beerId, String city) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price", Map.of("city", city));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();

            assertThat(actualJson).isEqualTo("[]");
        }

        @ParameterizedTest
        @ValueSource(strings = {"dgj", "sdif", ""})
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' [CITY_NOT_FOUND]")
        public void getBeersPriceInCityCityNotExistsTest(String city) {
            var getResponse = getRequest("/api/beer/5/beer-price", Map.of("city", city));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: '" + city + "'",
                    "/api/beer/5/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-532, 0, 3456956})
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' [CITY_NOT_FOUND]")
        public void getBeersPriceInCityBeerNotExistsTest(Long beerId) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price", Map.of("city", "Olsztyn"));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + beerId + "' id",
                    "/api/beer/" + beerId + "/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "-1, osk",
                "23909, PP",
                "0, 023"
        })
        @DisplayName("GET: '/api/beer/{beer_id}/beer-price' [CITY_NOT_FOUND]")
        public void getBeersPriceInCityBeerNotExistsAndCityNotExistsTest(Long beerId, String city) {
            var getResponse = getRequest("/api/beer/" + beerId + "/beer-price", Map.of("city", city));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: '%s'; Unable to find beer of '%d' id"
                            .formatted(city, beerId),
                    "/api/beer/" + beerId + "/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {1, 5, 2})
        @DisplayName("GET: '/api/store/{store_id}/beer-price' ordered")
        public void getBeerPricesFromStoreTest(Long storeId) {
            var getResponse = getRequest("/api/store/" + storeId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            Store store = getStore(storeId.longValue(), stores);
            List<BeerPriceResponseDTO> expected = store.getPrices().stream()
                    .map(BeerPriceResponseDTO::new)
                    .collect(Collectors.toList());
            sortByBeerIdAndPrice(expected);
            assertThat(actual).containsExactlyElementsOf(expected);
        }

        @ParameterizedTest
        @ValueSource(longs = {-1590, 0, 912345})
        @DisplayName("GET: '/api/store/{store_id}/beer-price' [STORE_NOT_FOUND]")
        public void getBeerPricesFromStoreNotExistsTest(Long beerId) {
            var getResponse = getRequest("/api/store/" + beerId + "/beer-price");

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '" + beerId + "' id",
                    "/api/store/" + beerId + "/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {7, 8, 9})
        @DisplayName("GET: '/api/store/{store_id}/beer-price' empty store")
        public void getBeerPricesFromStoreEmptyTest(Long storeId) {
            var getResponse = getRequest("/api/store/" + storeId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();

            assertThat(actualJson).isEqualTo("[]");
        }

        @Test
        @DisplayName("GET: '/api/beer-price' ordered")
        public void getBeerPricesAllArrayTest() {
            // when
            var getResponse = getRequestAuth("admin", "admin", "/api/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();
            List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

            // then
            List<BeerPriceResponseDTO> expected = new ArrayList<>();
            stores.forEach(store -> store.getPrices()
                    .forEach(price -> expected.add(new BeerPriceResponseDTO(price))));
            sortByCityBeerIdPriceAndStoreId(expected);
            assertThat(actual).containsExactlyElementsOf(expected);
        }
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    class PostRequestsParam {

        private List<Beer> beers;

        private List<Store> stores;

        @Autowired
        public PostRequestsParam(List<Beer> beers, List<Store> stores) {
            this.beers = beers;
            this.stores = stores;
        }

        @ParameterizedTest
        @CsvSource({
                "1, 9, 2.99",
                "5, 5, 3.09",
                "3, 2, 4.49"
        })
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price='")
        @DirtiesContext
        public void addBeerPriceIdTest(Long storeId, Long beerId, Double price) {
            // when
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    Map.of("beer_id", beerId.longValue(), "beer_price", price));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            // then
            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(beerId.longValue(), beers)),
                    createStoreResponse(getStore(storeId.longValue(), stores)),
                    EXPECTED_CURRENCY_UNIT + " " + price
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", beerId.longValue(), "store_id", storeId.longValue()));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(longs = {-546, 0, 8932})
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price=' [STORE_NOT_EXISTS]")
        public void addBeerPriceIdStoreNotExistsTest(Long storeId) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    Map.of("beer_id", 3L, "beer_price", 4.19));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '" + storeId + "' id",
                    "/api/store/" + storeId + "/beer-price");
        }

        @ParameterizedTest
        @ValueSource(doubles = {0d, -5.9d, -0.1d})
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price=' [PRICE_NON_POSITIVE]")
        public void addBeerPriceIdVolumeNegativeAndZeroTest(Double price) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    Map.of("beer_id", 5L, "beer_price", price));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -948, 6391})
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price=' [BEER_NOT_FOUND]")
        public void addBeerPriceIdBeerNotExistsTest(Long beerId) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/1/beer-price",
                    Map.of("beer_id", beerId.longValue(), "beer_price", 6.69));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + beerId + "' id",
                    "/api/store/1/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "2, 2, 4.99",
                "5, 2, 5.29",
                "6, 1, 5.09",
                "6, 6, 7.09"
        })
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price=' [BEER_PRICE_EXISTS]")
        public void addBeerPriceIdAlreadyExistsTest(Long storeId, Long beerId, Double price) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    Map.of("beer_id", beerId.longValue(), "beer_price", price));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer is already in store",
                    "/api/store/" + storeId + "/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "0, 1112",
                "564975, 564322",
                "-6, -921",
                "59236, 0"
        })
        @DisplayName("POST: '/api/store/{store_id}/beer-price?beer_id=?beer_price=' [BEER_n_STORE_NOT_FOUND]")
        public void addBeerPriceBeerAndStoreNotExistsTest(Long storeId, Long beerId) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    Map.of("beer_id", beerId.longValue(), "beer_price", "4.29"));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%d' id; Unable to find store of '%d' id"
                            .formatted(beerId, storeId),
                    "/api/store/" + storeId + "/beer-price");
        }
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    class PostRequestsObject {

        private List<Beer> beers;

        private List<Store> stores;

        @Autowired
        public PostRequestsObject(List<Beer> beers, List<Store> stores) {
            this.beers = beers;
            this.stores = stores;
        }

        @ParameterizedTest
        @CsvSource(value = {
                "4, 3, Tyskie Gronie, 0.65, 5.59",
                "1, 4, Zubr, null, 2.99",
                "7, 2, Ksiazece Zlote pszeniczne, null, 5.99",
                "6, 9, Perla Chmielowa Pils, 0.33, 3.09"
        }, nullValues = "null")
        @DisplayName("POST: '/api/store/{store_id}/beer-price'")
        @DirtiesContext
        public void addBeerPriceToStoreTest(Long storeId, Long beerId,
                                            String fullname, Double volume, Double price) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    createBeerPriceRequest(fullname, volume, price));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            String actualJson = postResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(beerId.longValue(), beers)),
                    createStoreResponse(getStore(storeId.longValue(), stores)),
                    EXPECTED_CURRENCY_UNIT + " " + price
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", beerId.longValue(), "store_id", storeId.longValue()));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "1, Miloslaw Biale, null, 5.79",
                "5, Tyskie Gronie, 0.65, 5.29",
                "1, Perla Chmielowa Pils, 0.5, 2.69",
                "2, Perla Chmielowa Pils, 0.33, 3.69"
        }, nullValues = "null")
        @DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BEER_PRICE_EXISTS]")
        public void addBeerPriceAlreadyExistsTest(Long storeId, String fullname,
                                                  Double volume, Double price) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    createBeerPriceRequest(fullname, volume, price));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer is already in store",
                    "/api/store/" + storeId + "/beer-price");
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1d, -0.5d, 0d})
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [VOLUME_NON_POSITIIVE]")
        public void createBeerPriceNegativeAndZeroVolumeTest(Double volume) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/6/beer-price",
                    createBeerPriceRequest("Tyskie Gronie", volume, 3.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/store/6/beer-price");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "  \n"})
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [BRAND_BLANK]")
        public void createBeerPriceBrandNullTest(String brand) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/5/beer-price",
                    createBeerPriceRequest(brand, 0.5, 3.09));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer (its name and type) was not specified",
                    "/api/store/5/beer-price");
        }

        @ParameterizedTest
        @ValueSource(doubles = {0d, -5.99d})
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [PRICE_NON_NEGATIVE]")
        public void createBeerPriceNonPositivePriceTest(Double price) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Kormoran Miodne", 0.5, price));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/store/2/beer-price");
        }

        @Test
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [PRICE_NULL]")
        public void createBeerPriceNullPriceTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/2/beer-price",
                    createBeerPriceRequest("Kormoran Wisniowe", 0.5, null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price was not specified",
                    "/api/store/2/beer-price");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "null, null, null, Beer (its name and type) was not specified; Price was not specified",
                "'  ', -1.33, 5.29, Beer (its name and type) was not specified; Volume must be a positive number",
                "Zatecky, -1, -2.99, Price must be a positive number; Volume must be a positive number"
        }, nullValues = "null")
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [COMBO]")
        public void createBeerPricePriceNullTest(String fullname, Double volume,
                                                 Double price, String errorMessage) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/7/beer-price",
                    createBeerPriceRequest(fullname, volume, price));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    errorMessage,
                    "/api/store/7/beer-price");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "Kormoran Miodne| null| Unable to find beer: [Kormoran Miodne, 0,50l]",
                "Kozel Cerny| 0.6| Unable to find beer: [Kozel Cerny, 0,60l]",
                "Miloslaw Ciemne| 0.5| Unable to find beer: [Miloslaw Ciemne, 0,50l]"
        }, nullValues = "null", delimiter = '|')
        @DisplayName("POST: '/api/store/{store_id}/beer-price' [BEER_NOT_FOUND]")
        public void createBeerPriceBeerNotExistsTest(String fullname, Double volume, String errMessage) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/8/beer-price",
                    createBeerPriceRequest(fullname, volume, 7.99));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    errMessage,
                    "/api/store/8/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-56234, 9021, 0})
        @DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [STORE_NOT_FOUND]")
        public void createBeerPriceStoreNotExistsTest(Long storeId) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    createBeerPriceRequest("Ksiazece Zlote pszeniczne", 0.5, 3.79));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '" + storeId + "' id",
                    "/api/store/" + storeId + "/beer-price");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "1155| Ksiazece Zielone| null| Unable to find beer: "
                        + "[Ksiazece Zielone, 0,50l]; Unable to find store of '1155' id",
                "-5| Lomza Pelne| 0.33| Unable to find beer: [Lomza Pelne, 0,33l]; "
                        + "Unable to find store of '-5' id",
                "0| Tyskie Gronie| 0.5| Unable to find beer: [Tyskie Gronie, 0,50l]; "
                        + "Unable to find store of '0' id"
        }, nullValues = "null", delimiter = '|')
        @DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [STORE_NOT_FOUND]")
        public void createBeerPriceStoreAndBeerNotExistsTest(Long storeId, String fullname,
                                                             Double volume, String errMessage) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store/" + storeId + "/beer-price",
                    createBeerPriceRequest(fullname, volume, 3.79));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    errMessage,
                    "/api/store/" + storeId + "/beer-price");
        }
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    class PatchRequests {

        private List<Beer> beers;

        private List<Store> stores;

        @Autowired
        public PatchRequests(List<Beer> beers, List<Store> stores) {
            this.beers = beers;
            this.stores = stores;
        }

        @ParameterizedTest
        @CsvSource({
                "4, 6, 5.89",
                "1, 1, 4.99",
                "6, 1, 5.09"
        })
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price='")
        @DirtiesContext
        public void updateBeerPricePriceTest(Long storeId, Long beerId, Double price) {
            // given
            var params = Map.of
                    ("beer_id", beerId.longValue(),
                            "store_id", storeId.longValue(),
                            "price", price);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

            // then
            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponse(getBeer(beerId.longValue(), beers)),
                    createStoreResponse(getStore(storeId.longValue(), stores)),
                    EXPECTED_CURRENCY_UNIT + " " + price
            );

            String expectedJson = toJsonString(expected);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getResponse = getRequest("/api/beer-price",
                    Map.of("beer_id", beerId.longValue(), "store_id", storeId.longValue()));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerPriceResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.2, 0d})
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [PRICE_NON_POSITIVE]")
        public void updateBeerPricePriceNegativeAndZeroTest(Double price) {
            // given
            var params = Map.of(
                    "store_id", 4L,
                    "beer_id", 1L,
                    "price", price
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Price must be a positive number",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "6, 2, 6.19",
                "2, 4, 2.89",
                "2, 9, 2.99"
        })
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [PRICE_SAME]")
        public void updateBeerPricePropertiesSameTest(Long storeId, Long beerId, Double price) {
            // given
            var params = Map.of(
                    "store_id", storeId,
                    "beer_id", beerId,
                    "price", price
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "The price is '%s' already".formatted(EXPECTED_CURRENCY_UNIT + " " + price),
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-51245, 0, 968532})
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [BEER_NOT_FOUND]")
        public void updateBeerPriceBeerNotFound(Long beerId) {
            // given
            var params = Map.of(
                    "store_id", 4,
                    "beer_id", beerId,
                    "price", 4.49
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%d' id".formatted(beerId),
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-5203, 0, 2039})
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [STORE_NOT_FOUND]")
        public void updateBeerPriceStoreNotFound(Long storeId) {
            // given
            var params = Map.of(
                    "store_id", storeId,
                    "beer_id", 3,
                    "price", 3.99
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '%d' id".formatted(storeId),
                    "/api/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "0, 253980",
                "21359, -5",
                "-695842, 0"
        })
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [BEER_n_STORE_404]")
        public void updateBeerPriceStoreAndBeerNotFound(Long storeId, Long beerId) {
            // given
            var params = Map.of(
                    "store_id", storeId,
                    "beer_id", beerId,
                    "price", 6.99
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%d' id; Unable to find store of '%d' id"
                            .formatted(beerId, storeId),
                    "/api/beer-price");
        }

        @ParameterizedTest
        @CsvSource({
                "7, 8",
                "1, 4",
                "8, 1",
                "4, 3"
        })
        @DisplayName("PATCH: '/api/beer-price?store_id=?beer_id=?price=' [PRICE_NOT_FOUND]")
        public void updateBeerPricePriceNotFound(Long storeId, Long beerId) {
            // given
            var params = Map.of(
                    "store_id", storeId,
                    "beer_id", beerId,
                    "price", 5.29
            );
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer-price", params);
            String jsonResponse = patchResponse.getBody();

            // then
            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Store does not currently sell this beer",
                    "/api/beer-price");
        }
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestMethodOrder(MethodOrderer.Random.class)
    class DeleteRequests {

        private List<Beer> beers;

        private List<Store> stores;

        private List<BeerPrice> prices;

        @Autowired
        public DeleteRequests(List<Beer> beers, List<Store> stores, List<BeerPrice> beerPrices) {
            this.beers = beers;
            this.stores = stores;
            this.prices = beerPrices;
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "6, 2",
                "4, 6",
        })
        @DisplayName("DELETE: '/api/beer-price?store_id=?beer_id='")
        @DirtiesContext
        public void deleteBeerPriceTest(Long storeId, Long beerId) {
            // given
            var params = Map.of(
                    "store_id", storeId,
                    "beer_id", beerId
            );
            // when
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer-price", params);
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = deleteResponse.getBody();
            BeerPriceDeleteDTO actual = toModel(actualJson, BeerPriceDeleteDTO.class);

            // then
            BeerPriceDeleteDTO expected = createBeerPriceDeleteResponse(
                    createBeerResponse(getBeer(beerId.longValue(), beers)),
                    createStoreResponse(getStore(storeId.longValue(), stores)),
                    getBeerPrice(storeId, beerId, prices).getPrice().toString(),
                    "Beer price was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getRequest = getRequest("/api/beer-price", Map.of("beer_id", beerId, "store_id", storeId));

            String jsonResponse = getRequest.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Store does not currently sell this beer",
                    "/api/beer-price");
        }

        @ParameterizedTest
        @ValueSource(longs = {-5238, 0, 9812344})
        @DisplayName("DELETE: '/api/beer-price?store_id=?beer_id=' [STORE_NOT_FOUND]")
        public void deleteBeerPriceStoreNotExistsTest(Long storeId) {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer-price", Map.of("store_id", storeId, "beer_id", 3L));

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '%d' id".formatted(storeId),
                    "/api/beer-price"
            );
        }

        @ParameterizedTest
        @ValueSource(longs = {-95624, 0, 25398})
        @DisplayName("DELETE: '/api/beer-price?store_id=?beer_id=' [BEER_NOT_FOUND]")
        public void deleteBeerPriceBeerNotExistsTest(Long beerId) {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer-price", Map.of("store_id", 3L, "beer_id", beerId));

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%d' id".formatted(beerId),
                    "/api/beer-price"
            );
        }

        @ParameterizedTest
        @CsvSource({
                "2, 1",
                "6, 7",
                "8, 9",
                "3, 4"
        })
        @DisplayName("DELETE: '/api/beer-price?store_id=?beer_id=' [BEER_PRICE_NOT_FOUND]")
        public void deleteBeerPricePriceNotExistsTest(Long storeId, Long beerId) {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer-price", Map.of("store_id", storeId, "beer_id", beerId));

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Store does not currently sell this beer",
                    "/api/beer-price"
            );
        }

        @ParameterizedTest
        @CsvSource({
                "-921743, 3888",
                "0, -5283",
                "5238, 0"
        })
        @DisplayName("DELETE: '/api/beer-price?store_id=?beer_id=' [STORE_n_BEER_404]")
        public void deleteBeerPriceStoreAndBeerNotFoundTest(Long storeId, Long beerId) {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer-price", Map.of("store_id", storeId, "beer_id", beerId));

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '%d' id; Unable to find store of '%d' id"
                            .formatted(beerId, storeId),
                    "/api/beer-price"
            );
        }
    }

    @Nested
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    @TestClassOrder(ClassOrderer.Random.class)
    class OtherControllerRequests {

        @Nested
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        @TestMethodOrder(MethodOrderer.Random.class)
        class BeerRequests {

            private List<Beer> beers;

            @Autowired
            public BeerRequests(List<Beer> beers) {
                this.beers = beers;
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "6, Ksiazece, null, 0.6",
                    "2, Namyslow, Chmielowe, null",
                    "4, null, Ciemnozloty, null"
            }, nullValues = "null")
            @DisplayName("PATCH: '/api/beer/{beer_id}' brand/type update removes prices")
            @DirtiesContext
            public void updateBeerBrandAndOrTypeRemovesPricesTest(Long beerId, String brand, String type, Double volume) {
                // given
                BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);
                var prices = getBeer(beerId.longValue(), beers).getPrices();
                assertThat(prices).isNotEmpty();

                // when
                var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
                String actualJson = getResponse.getBody();
                assertThat(actualJson).isEqualTo("[]");
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "2, 0.33",
                    "3, 0.5"
            })
            @DisplayName("PATCH: '/api/beer/{beer_id}' volume update does not remove prices")
            @DirtiesContext
            public void updateBeerVolumeDoesNotRemovePricesTest(Long beerId, Double volume) {
                // given
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, volume);
                List<BeerPriceResponseDTO> prices = getBeer(beerId.longValue(), beers)
                        .getPrices()
                        .stream()
                        .map(BeerPriceResponseDTO::new)
                        .toList();
                assertThat(prices).isNotEmpty();

                // when
                var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                prices.stream().forEach(price -> price.getBeer().setVolume(volume));
                var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
                String actualJson = getResponse.getBody();
                List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);
                assertThat(actual).hasSameElementsAs(prices);
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "2, Kasztelan, Niepasteryzowane, null",
                    "6, Zywiec, Porter, 0.6"
            }, nullValues = "null")
            @DisplayName("PUT: '/api/beer/{beer_id}' replacing beer removes prices")
            @DirtiesContext
            public void replaceBeerRemovesPricesTest(Long beerId, String brand, String type, Double volume) {
                // given
                BeerRequestDTO request = createBeerRequest(brand, type, volume);
                var prices = getBeer(beerId.longValue(), beers).getPrices();
                assertThat(prices).isNotEmpty();

                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
                String actualJson = getResponse.getBody();
                assertThat(actualJson).isEqualTo("[]");
            }

            @ParameterizedTest
            @ValueSource(longs = {1, 4})
            @DisplayName("DELETE: '/api/beer/{beer_id}' deleting beer removes prices")
            @DirtiesContext
            public void deleteBeerByIdRemovesPricesTest(Long beerId) {
                assertThat(getBeer(beerId.longValue(), beers).getPrices()).isNotEmpty();
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId);
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                var getResponse = getRequestAuth("admin", "admin", "/api/beer-price");
                List<BeerPriceResponseDTO> prices = toModelList
                        (getResponse.getBody(), BeerPriceResponseDTO.class);
                List<BeerPriceResponseDTO> pricesOfBeer = prices.stream()
                        .filter(price -> price.getBeer().getId().equals(beerId))
                        .toList();
                assertThat(pricesOfBeer).isEmpty();
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "9, Perla, Chmielowa Pils, 0.33",
                    "6, Miloslaw, Biale, null",
                    "2, Ksiazece, Zlote pszeniczne, 0.5"
            }, nullValues = "null")
            @DisplayName("DELETE: '/api/beer' deleting beer removes prices")
            @DirtiesContext
            public void deleteBeerByObjectRemovesPricesTest(Long beerId, String brand, String type, Double volume) {
                // given
                BeerRequestDTO request = createBeerRequest(brand, type, volume);
                assertThat(getBeer(beerId.longValue(), beers).getPrices()).isNotEmpty();
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                // then
                var getResponse = getRequestAuth("admin", "admin", "/api/beer-price");
                List<BeerPriceResponseDTO> prices = toModelList
                        (getResponse.getBody(), BeerPriceResponseDTO.class);
                List<BeerPriceResponseDTO> pricesOfBeer = prices.stream()
                        .filter(price -> price.getBeer().getId().equals(beerId))
                        .toList();
                assertThat(pricesOfBeer).isEmpty();
            }
        }

        @Nested
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        @TestMethodOrder(MethodOrderer.Random.class)
        class StoreRequests {

            private List<Store> stores;

            @Autowired
            public StoreRequests(List<Store> stores) {
                this.stores = stores;
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "1, null, null, ul. Barcza 5",
                    "4, Lubi, null, null",
                    "5, null, Wroclaw, null",
                    "6, Piwiarnia, Gdansk, ul. Galczynskiego 7"
            }, nullValues = "null")
            @DirtiesContext
            @DisplayName("PATCH: '/api/store/{store_id}' updating store removes prices")
            public void updateStoreRemovesPricesTest(Long storeId, String name, String city, String street) {
                // given
                StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
                assertThat(getStore(storeId.longValue(), stores).getPrices()).isNotEmpty();

                // when
                var patchResponse = patchRequestAuth("admin", "admin", "/api/store/" + storeId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                var getResponse = getRequest("/api/store/" + storeId + "/beer-price");
                String actualJson = getResponse.getBody();
                assertThat(actualJson).isEqualTo("[]");
            }

            @ParameterizedTest
            @CsvSource({
                    "3, Biedronka, Olsztyn, ul. Iwaszkiewicza 1",
                    "6, ABC, Rzeszow, ul. Polna 1",
                    "4, Lubi, Krakow, ul. Nowaka 5"
            })
            @DirtiesContext
            @DisplayName("PUT: '/api/store/{store_id}' replacing store removes prices")
            public void replaceStoreRemovesPricesTest(Long storeId, String name, String city, String street) {
                // given
                StoreRequestDTO request = createStoreRequest(name, city, street);
                assertThat(getStore(storeId, stores).getPrices()).isNotEmpty();

                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                // then
                var getResponse = getRequest("/api/store/" + storeId + "/beer-price");
                String actualJson = getResponse.getBody();
                assertThat(actualJson).isEqualTo("[]");
            }

            @ParameterizedTest
            @ValueSource(longs = {5, 1, 2})
            @DirtiesContext
            @DisplayName("DELETE: '/api/store/{store_id}' deleting store removes prices")
            public void deleteStoreRemovesPricesTest(Long storeId) {
                assertThat(getStore(storeId.longValue(), stores).getPrices()).isNotEmpty();
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/" + storeId);
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                // then
                var getResponse = getRequestAuth("admin", "admin", "/api/beer-price");
                List<BeerPriceResponseDTO> prices = toModelList(getResponse.getBody(), BeerPriceResponseDTO.class);
                List<BeerPriceResponseDTO> pricesOfStore = prices.stream()
                        .filter(price -> price.getStore().getId().equals(storeId))
                        .toList();
                assertThat(pricesOfStore).isEmpty();
            }
        }
    }

    private void sortByBeerIdPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
        Comparator<Object> comparator = Comparator
                .comparing(p -> ((BeerPriceResponseDTO) p).getBeer().getId())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
        Collections.sort(pricesDTO, comparator);
    }

    private void sortByCityPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
        Comparator<Object> comparator = Comparator
                .comparing(p -> ((BeerPriceResponseDTO) p).getStore().getCity())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
        Collections.sort(pricesDTO, comparator);
    }

    private void sortByPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
        Comparator<Object> comparator = Comparator
                .comparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
        Collections.sort(pricesDTO, comparator);
    }

    private void sortByBeerIdAndPrice(List<BeerPriceResponseDTO> pricesDTO) {
        Comparator<Object> comparator = Comparator
                .comparing(p -> ((BeerPriceResponseDTO) p).getBeer().getId())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly());
        Collections.sort(pricesDTO, comparator);
    }

    private void sortByCityBeerIdPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
        Comparator<Object> comparator = Comparator
                .comparing(p -> ((BeerPriceResponseDTO) p).getStore().getCity())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getBeer().getId())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
                .thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
        Collections.sort(pricesDTO, comparator);
    }
}
