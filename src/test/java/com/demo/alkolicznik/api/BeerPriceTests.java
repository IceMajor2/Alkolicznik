package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
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

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get beer prices of city")
        public void getBeerPricesFromCityTest() {
            var getResponse = getRequest("/api/beer-prce", Map.of("city", "Olsztyn"));
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
            assertThat(actual.toArray()).containsExactly(expected.toArray());
            assertThat(actualJson).isEqualTo(expectedJson);
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
                    "Unable to find store of 8 id",
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
    class PostRequests {

        @Test
        @DisplayName("Valid add beer to store")
        @DirtiesContext
        public void addBeerPriceToStoreTest() {
            var postResponse = postRequest("/api/store/{id}/beer",
                    createBeerPriceRequest("Perla Chmielowa Pils", 3.69),
                    2);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();

            BeerPriceResponseDTO expected = createBeerPriceResponse(
                    createBeerResponseDTO(1L, "Perla Chmielowa Pils", 0.5),
                    createStoreResponseDTO(2L, "Biedronka", "Olsztyn", "ul. Sikorskiego-Wilczynskiego 12"), 3.69
            );
            String expectedJson = toJsonString(expected);

            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }
}
