package com.demo.alkolicznik.security;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.ResponseTestUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class SecuredEndpointTests {

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
        @DisplayName("ANONYMOUS: update beer")
        public void whenAnonUpdatesBeer_thenReturn404Test() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
            var putResponse = putRequestAuth("user", "user", "/api/beer/{id}", request, 2L);

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
            var deleteResponse = deleteRequestAuth("user", "user",
                    "/api/beer/{id}", 2L);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Resource not found",
                    "/api/beer/2"
            );
        }

        //        @Test
//        @DisplayName("ANONYMOUS: delete beer (fields)")

//        @Test
//        @DisplayName("ANONYMOUS: creates beer")

//        @Test
//        @DisplayName("USER: get beers")

//        @Test
//        @DisplayName("USER: update beer")

//        @Test
//        @DisplayName("USER: delete beer (ID)")

        //        @Test
//        @DisplayName("ADMIN: delete beer (fields)")

        //        @Test
//        @DisplayName("ACCOUNTANT: get beers")

        //        @Test
//        @DisplayName("ACCOUNTANT: update beer")

        //        @Test
//        @DisplayName("ACCOUNTANT: delete beer (ID)")

        //        @Test
//        @DisplayName("ADMIN: delete beer (fields)")

        //        @Test
//        @DisplayName("ADMIN: get beers")

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
