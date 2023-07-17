package com.demo.alkolicznik.api;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.*;
import static com.demo.alkolicznik.utils.requests.MockRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@AutoConfigureMockMvc
public class StoreTests {

    @Autowired
    private List<Store> stores;

    public static MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        StoreTests.mockMvc = mockMvc;
    }

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

        @Test
        @DisplayName("GET: '/api/store'")
        @WithUserDetails("admin")
        public void getStoresAllTest() {
            List<StoreResponseDTO> expected = stores.stream()
                    .map(StoreResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockGetRequest("/api/store"),
                    HttpStatus.OK, expectedJson);
            List<StoreResponseDTO> actual = toModelList(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class PutRequests {

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' update name")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateStoreNameTest() {
            StoreUpdateDTO request = createStoreUpdateRequest("Carrefour Express", null, null);

            StoreResponseDTO expected = createStoreResponse(1L, "Carrefour Express", "Olsztyn", "ul. Barcza 4");
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockPutRequest("/api/store/1", request),
                    HttpStatus.NO_CONTENT,
                    expectedJson);
            StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/store/1");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' update city")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateStoreCityTest() {
            StoreUpdateDTO request = createStoreUpdateRequest(null, "Gdynia", null);

            StoreResponseDTO expected = createStoreResponse(7L, "Tesco", "Gdynia", "ul. Morska 22");
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockPutRequest("/api/store/7", request),
                    HttpStatus.NO_CONTENT,
                    expectedJson);
            StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/store/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' update street")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateStoreStreetTest() {
            StoreUpdateDTO request = createStoreUpdateRequest(null, null, "ul. Zeromskiego 4");

            StoreResponseDTO expected = createStoreResponse(4L, "ABC", "Warszawa", "ul. Zeromskiego 4");
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockPutRequest("/api/store/4", request),
                    HttpStatus.NO_CONTENT,
                    expectedJson);
            StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/store/4");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, StoreResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' [NAME_BLANK]")
        public void updateStoreNameBlankTest() {
            StoreUpdateDTO request = createStoreUpdateRequest("", null, null);
            var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Name was not specified",
                    "/api/store/4"
            );

            request = createStoreUpdateRequest("\t\n ", null, null);
            putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Name was not specified",
                    "/api/store/4"
            );
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' [PROPERTIES_SAME]")
        public void updateStorePropertiesSameTest() {
            StoreUpdateDTO request = createStoreUpdateRequest("Lubi", "Warszawa", "ul. Nowaka 5");
            var putResponse = putRequestAuth("admin", "admin",
                    "/api/store/5", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/store/5");
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' [STREET_BLANK]")
        public void updateStoreStreetBlankTest() {
            StoreUpdateDTO request = createStoreUpdateRequest(null, null, "");
            var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Street was not specified",
                    "/api/store/4"
            );

            request = createStoreUpdateRequest(null, null, "\t\n ");
            putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Street was not specified",
                    "/api/store/4"
            );
        }

        @Test
        @DisplayName("PUT: '/api/store/{store_id}' [CITY_BLANK]")
        public void updateStoreCityBlankTest() {
            StoreUpdateDTO request = createStoreUpdateRequest(null, "", null);
            var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "City was not specified",
                    "/api/store/4"
            );

            request = createStoreUpdateRequest(null, "\t\n ", null);
            putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

            jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "City was not specified",
                    "/api/store/4"
            );
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
        public void createStoreNameNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest(null, "Mragowo", "ul. Wyspianskiego 17"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [NAME_BLANK]")
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
        public void createStoreCityNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Lubi", null, "ul. Kwiatkowa 3"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [CITY_BLANK]")
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
        public void createStoreStreetNullTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest("Primo", "Olsztyn", null));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [STREET_BLANK]")
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
        public void createStoreAlreadyExistsTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store", createStoreRequest("Lidl", "Olsztyn", "ul. Iwaszkiewicza 1"));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.CONFLICT, "Store already exists", "/api/store");
        }

        @Test
        @DisplayName("POST: '/api/store' [NAME_BLANK; STREET_NULL; STREET_EMPTY]")
        public void createStoreNameBlankCityNullStreetEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/store",
                    createStoreRequest(" \t", null, ""));

            String json = postResponse.getBody();

            assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified;" +
                    " Name was not specified; Street was not specified", "/api/store");
        }
    }

    @Nested
    class DeleteRequests {

        @Test
        @DisplayName("DELETE: '/api/store/{store_id}'")
        @DirtiesContext
        @WithUserDetails("admin")
        public void deleteStoreTest() {
            StoreDeleteDTO expected = createStoreDeleteResponse(
                    getStore(6L, stores),
                    "Store was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockDeleteRequest("/api/store/6"),
                    HttpStatus.OK,
                    expectedJson);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getRequest = getRequest("/api/store/6");

            String jsonResponse = getRequest.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '6' id",
                    "/api/store/6");
        }

        @Test
        @DisplayName("DELETE: '/api/store/{store_id}' [STORE_NOT_FOUND]")
        public void deleteStoreNotExistsTest() {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/store/0");

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find store of '0' id",
                    "/api/store/0"
            );
        }
    }
}
