package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
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
import java.util.Map;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class StoreTests {

    @Autowired
    private List<Store> stores;

    @Nested
    class GetRequests {

        @Test
        @DisplayName("GET: '/api/store/{store_id}'")
        public void getStoreTest() {
            var getResponse = getRequest("/api/store/3");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

            StoreResponseDTO expected = createStoreResponse(3L, "Lidl", "Olsztyn", "ul. Iwaszkiewicza 1");
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("GET: '/api/store/{store_id}' [STORE_NOT_FOUND]")
        public void getStoreNotExistingTest() {
            var getResponse = getRequest("/api/store/9999");

            String json = getResponse.getBody();

            assertIsError(json,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '9999' id",
                    "/api/store/9999");
        }

        @Test
        @DisplayName("GET: '/api/store' of city")
        public void getStoreFromCityArrayTest() {
            var getResponse = getRequest("/api/store", Map.of("city", "Warszawa"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            List<StoreResponseDTO> actual = toModelList(jsonResponse, StoreResponseDTO.class);

            List<StoreResponseDTO> expected = stores.stream()
                    .filter(store -> store.getCity().equals("Warszawa"))
                    .map(StoreResponseDTO::new)
                    .toList();
            assertThat(actual.toArray()).containsExactly(expected.toArray());
        }

        @Test
        @DisplayName("GET: '/api/store/{store_id}' of city [CITY_NOT_FOUND]")
        public void getStoreFromCityNotExistsArrayTest() {
            var getResponse = getRequest("/api/store", Map.of("city", "Ciechanow"));

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: 'Ciechanow'",
                    "/api/store");
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("POST: '/api/store'")
        @DirtiesContext
        public void createStoreTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Dwojka", "Krakow", "ul. Powstancow 9"));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

            StoreResponseDTO expected = createStoreResponse(8L, "Dwojka", "Krakow", "ul. Powstancow 9");
            String expectedJson = toJsonString(expected);

            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/store/8");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/store' [NAME_NULL]")
        // @DirtiesContext
        public void createStoreNameNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest(null, "Mragowo", "ul. Wyspianskiego 17"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [NAME_BLANK]")
        // @DirtiesContext
        public void createStoreNameBlankAndEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest("", "Gdansk", "ul. Hallera 120"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/store");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest("\t \t   \n", "Sopot", "ul. Olsztynska 1"));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [CITY_NULL]")
        // @DirtiesContext
        public void createStoreCityNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Lubi", null, "ul. Kwiatkowa 3"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [CITY_BLANK]")
        // @DirtiesContext
        public void createStoreCityBlankAndEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Lubi", "", "ul. Kwiatkowa 3"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Lubi", "\t \n", "ul. Brzozowa 31"));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [STREET_NULL]")
        // @DirtiesContext
        public void createStoreStreetNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Primo", "Olsztyn", null));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [STREET_BLANK]")
        // @DirtiesContext
        public void createStoreStreetBlankAndEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Primo", "Olsztyn", ""));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Primo", "Olsztyn", "\t     "));

            json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [STORE_EXISTS]")
        // @DirtiesContext
        public void createStoreAlreadyExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest("Lidl", "Olsztyn", "ul. Iwaszkiewicza 1"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.CONFLICT, "Store already exists", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [NAME_BLANK; STREET_NULL; STREET_EMPTY]")
        // @DirtiesContext
        public void createStoreNameBlankCityNullStreetEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest(" \t", null, ""));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified;" +
                    " Name was not specified; Street was not specified", "/api/store");
        }
    }
}
