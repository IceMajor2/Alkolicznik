package com.demo.alkolicznik.security;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.ResponseTestUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class SecuredEndpointTests {

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

    @Autowired
    private TestRestTemplate restTemplate;

    @Nested
    class BeerController {

        @Test
        @DisplayName("ANONYMOUS: get beers")
        public void whenAnonGetsBeers_thenReturn404Test() {
            var getResponse = getRequest("/api/beer");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/beer");
        }

        @Test
        @DisplayName("ANONYMOUS: creates beer")
        public void whenAnonCreatesBeer_thenReturn404Test() {
            BeerRequestDTO request = createBeerRequest(beers.get(1));
            var postResponse = restTemplate
                    .postForEntity("/api/beer", request, String.class);

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/beer");
        }

        @Test
        @DisplayName("ANONYMOUS: update beer")
        public void whenAnonUpdatesBeer_thenReturn404Test() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
            var putResponse = restTemplate.exchange("/api/beer/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    String.class,
                    2L);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer/2"
            );
        }

        @Test
        @DisplayName("ANONYMOUS: delete beer (id)")
        public void whenAnonDeletesBeer_thenReturn404Test() {
            var deleteResponse = restTemplate.exchange("/api/beer/{id}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    String.class,
                    2L);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer/2"
            );
        }

        @Test
        @DisplayName("ANONYMOUS: delete beer (fields)")
        public void whenAnonDeletesBeerByFields_thenReturn404Test() {
            BeerRequestDTO request = createBeerRequest(getBeer(3L, beers));
            var deleteResponse = restTemplate.exchange("/api/beer",
                    HttpMethod.DELETE,
                    new HttpEntity<>(request),
                    String.class);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer"
            );
        }

        @Test
        @DisplayName("USER: get beers")
        public void whenUserGetsBeers_thenReturn404Test() {
            var getResponse = restTemplate
                    .withBasicAuth("user", "user")
                    .getForEntity("/api/beer", String.class);

            String json = getResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer");
        }

        @Test
        @DisplayName("USER: creates beer")
        public void whenUserCreatesBeers_thenReturn404Test() {
            BeerRequestDTO request = createBeerRequest(beers.get(1));
            var postResponse = postRequestAuth("user", "user", "/api/beer", request);

            String json = postResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer");
        }

        @Test
        @DisplayName("USER: update beer")
        public void whenUserUpdatesBeer_thenReturn404Test() {
            BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);
            var putResponse = putRequestAuth("user", "user", "/api/beer/{id}", request, 1L);

            String json = putResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer/1");
        }

        @Test
        @DisplayName("USER: delete beer (ID)")
        public void whenUserDeletesBeer_thenReturn404Test() {
            var deleteResponse = deleteRequestAuth("user", "user",
                    "/api/beer/{id}", 1L);

            String json = deleteResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer/1");
        }

        @Test
        @DisplayName("USER: delete beer (fields)")
        public void whenUserDeletesBeerByFields_thenReturn404Test() {
            BeerRequestDTO request = createBeerRequest(beers.get(1));
            var deleteResponse = deleteRequestAuth("user", "user",
                    request,
                    "/api/beer");

            String json = deleteResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer");
        }

        //        @Test
//        @DisplayName("ACCOUNTANT: get beers")
        @Test
        @DisplayName("ACCOUNTANT: creates beer")
        public void whenAccountantCreatesBeer_thenReturn404Test() {

        }
        //        @Test
//        @DisplayName("ACCOUNTANT: update beer")

        //        @Test
//        @DisplayName("ACCOUNTANT: delete beer (ID)")

        //        @Test
//        @DisplayName("ACCOUNTANT: delete beer (fields)")

        //        @Test
//        @DisplayName("ADMIN: get beers")
        @Test
        @DisplayName("ADMIN: creates beer")
        public void whenAdminCreatesBeer_thenReturn404Test() {

        }
        //        @Test
//        @DisplayName("ADMIN: update beer")

        //        @Test
//        @DisplayName("ADMIN: delete beer (ID)")

        //        @Test
//        @DisplayName("ADMIN: delete beer (fields)")
    }

    @Nested
    class StoreController {

        @Test
        @DisplayName("ANONYMOUS: get stores")
        public void whenAnonGetsStores_thenReturn404Test() {
            var getResponse = getRequest("/api/store");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/store");
        }

        @Test
        @DisplayName("ANONYMOUS: update store")
        public void whenAnonUpdatesStore_thenReturn404Test() {
            StoreUpdateDTO request = createStoreUpdateRequest("Lubi", null, null);
            var putResponse = putRequestAuth("user", "user", "/api/store/{id}", request, 4L);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/store/4"
            );
        }

        @Test
        @DisplayName("ANONYMOUS: delete store")
        public void whenAnonDeletesStore_thenReturn404Test() {
            var deleteResponse = deleteRequestAuth("user", "user",
                    "/api/store/{id}", 2L);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/store/2"
            );
        }

//        @Test
//        @DisplayName("USER: get stores")

        //        @Test
//        @DisplayName("USER: update store")

        //        @Test
//        @DisplayName("USER: delete store")

        //        @Test
//        @DisplayName("ACCOUNTANT: get stores")

        //        @Test
//        @DisplayName("ACCOUNTANT: update store")

        //        @Test
//        @DisplayName("ACCOUNTANT: delete store")

        //        @Test
//        @DisplayName("ADMIN: get stores")

        //        @Test
//        @DisplayName("ADMIN: update store")

        //        @Test
//        @DisplayName("ADMIN: delete store")
    }

    @Nested
    class BeerPriceController {

        @Test
        @DisplayName("ANONYMOUS: get prices")
        public void whenAnonGetsPrices_thenReturn404Test() {
            var getResponse = getRequest("/api/beer-price");

            String json = getResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer-price");
        }

        //        @Test
//        @DisplayName("ANONYMOUS: create price by JSON")

        //        @Test
//        @DisplayName("ANONYMOUS: create price by URL parameters")

        @Test
        @DisplayName("ANONYMOUS: update price")
        public void whenAnonUpdatesPrice_thenReturn404Test() {
            BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(7.89);
            var putResponse = putRequestAuth("user", "user",
                    "/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 2L));

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer-price"
            );
        }

        @Test
        @DisplayName("ANONYMOUS: delete price")
        public void whenAnonDeletesPrice_thenReturn404Test() {

            var deleteResponse = deleteRequestAuth("user", "user",
                    "/api/beer-price", Map.of("beer_id", 2L, "store_id", 5L));

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer-price"
            );
        }


        //        @Test
//        @DisplayName("USER: update store")

        //        @Test
//        @DisplayName("USER: delete store")

        //        @Test
//        @DisplayName("USER: create price by JSON")

        //        @Test
//        @DisplayName("USER: create price by URL parameters")

        //        @Test
//        @DisplayName("ACCOUNTANT: get stores")

        //        @Test
//        @DisplayName("ACCOUNTANT: update store")

        //        @Test
//        @DisplayName("ACCOUNTANT: delete store")

        //        @Test
//        @DisplayName("ACCOUNTANT: create price by JSON")

        //        @Test
//        @DisplayName("ACCOUNTANT: create price by URL parameters")

        //        @Test
//        @DisplayName("ADMIN: get stores")

        //        @Test
//        @DisplayName("ADMIN: update store")

        //        @Test
//        @DisplayName("ADMIN: delete store")

        //        @Test
//        @DisplayName("ADMIN: create price by JSON")

        //        @Test
//        @DisplayName("ADMIN: create price by URL parameters")
    }
}
