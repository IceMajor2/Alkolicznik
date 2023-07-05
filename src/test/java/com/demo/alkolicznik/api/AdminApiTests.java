package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.demo.alkolicznik.dto.delete.BeerDeleteResponseDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
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

import java.util.ArrayList;
import java.util.List;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.ResponseUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@AutoConfigureMockMvc
public class AdminApiTests {

    @Autowired
    private List<Store> stores;

    @Autowired
    private List<Beer> beers;

    public static MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        AdminApiTests.mockMvc = mockMvc;
    }

    @Nested
    class BeerTests {

        @Nested
        class GetRequests {

            @Test
            @DisplayName("Get all stored beers in array")
            @WithUserDetails("admin")
            public void getBeerAllArrayAuthorizedTest() throws Exception {
                List<BeerResponseDTO> expected = beers.stream()
                        .map(BeerResponseDTO::new)
                        .toList();
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockGetRequest("/api/admin/beer"),
                        HttpStatus.OK,
                        expectedJson);
                List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Get all beers in array: UNAUTHORIZED")
            public void getBeerAllArrayUnauthorizedTest() {
                var getResponse = getRequest("/api/admin/beer");

                String json = getResponse.getBody();

                assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer");
            }
        }

        @Nested
        class PutRequests {

            @Test
            @DisplayName("Update beer: VOLUME")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerVolumeTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.5);

                BeerResponseDTO expected = createBeerResponse(3L, "Tyskie Gronie", 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 3L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Update beer: BRAND")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerBrandTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);

                BeerResponseDTO expected = createBeerResponse(3L, "Ksiazece Gronie", 0.6);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 3L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Update beer: TYPE")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerTypeTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "IPA", null);

                BeerResponseDTO expected = createBeerResponse(2L, "Ksiazece IPA", 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 2L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Invalid beer update: request EMPTY")
            public void updateBeerEmptyRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 6L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "No property to update was specified",
                        "/api/admin/beer/6"
                );
            }

            @Test
            @DisplayName("Invalid beer update: VOLUME negative and zero")
            public void updateBeerVolumeNegativeAndZeroRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0d);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Volume must be a positive number",
                        "/api/admin/beer/4"
                );

                request = createBeerUpdateRequest(null, null, -5.1d);
                putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 4L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Volume must be a positive number",
                        "/api/admin/beer/4"
                );
            }

            @Test
            @DisplayName("Invalid beer update: BEER_NOT_EXISTS")
            public void updateBeerNotExistsRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 321L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '321' id",
                        "/api/admin/beer/321"
                );
            }

            @Test
            @DisplayName("Invalid beer update: TYPE blank")
            public void updateBeerTypeBlankRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "\t \n", null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 5L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Type was not specified",
                        "/api/admin/beer/5"
                );

                request = createBeerUpdateRequest(null, "", null);
                putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 5L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Type was not specified",
                        "/api/admin/beer/5"
                );
            }

            @Test
            @DisplayName("Invalid beer update: BRAND blank")
            public void updateBeerBrandBlankRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("\t \t \n\n\n", null, null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 5L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Brand was not specified",
                        "/api/admin/beer/5"
                );

                request = createBeerUpdateRequest("", null, null);
                putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 5L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Brand was not specified",
                        "/api/admin/beer/5"
                );
            }

            @Test
            @DisplayName("Invalid beer update: NOT_AUTHORIZED")
            public void updateBeerUnauthorizedTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
                var putResponse = putRequestAuth("user", "user", "/api/admin/beer/{id}", request, 2L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/beer/2"
                );
            }

            @Test
            @DisplayName("Invalid beer update: PROPERTIES_SAME")
            public void updateBeerUnchangedTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Komes", "Malinowe", 0.33);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 5L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/admin/beer/5"
                );
            }
        }

        @Nested
        class DeleteRequests {

            @Test
            @DisplayName("Delete beer")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteBeerTest() {
                BeerDeleteResponseDTO expected = createBeerDeleteResponse(
                        getBeer(6L, beers),
                        "Beer was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockDeleteRequest("/api/admin/beer/{id}", 6L),
                        HttpStatus.OK,
                        expectedJson);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Invalid delete beer: UNAUTHORIZED")
            public void deleteBeerUnauthorizedTest() {
                var deleteResponse = deleteRequestAuth("user", "user",
                        "/api/admin/beer/{id}", 2L);

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/beer/2"
                );
            }

            @Test
            @DisplayName("Invalid delete beer: BEER_NOT_EXISTS")
            public void deleteBeerNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/admin/beer/{id}", 0L);

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '0' id",
                        "/api/admin/beer/0"
                );
            }
        }
    }

    @Nested
    class StoreTests {

        @Nested
        class GetRequests {

            @Test
            @DisplayName("Get all stores")
            @WithUserDetails("admin")
            public void getStoresAllTest() {
                List<StoreResponseDTO> expected = stores.stream()
                        .map(StoreResponseDTO::new)
                        .toList();
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockGetRequest("/api/admin/store"),
                        HttpStatus.OK, expectedJson);
                List<StoreResponseDTO> actual = toModelList(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Get all stores: UNAUTHORIZED")
            public void getStoresAllUnauthorizedTest() {
                var getResponse = getRequest("/api/admin/store");

                String json = getResponse.getBody();

                assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/store");
            }
        }

        @Nested
        class PutRequests {

            @Test
            @DisplayName("Update store: NAME")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateStoreNameTest() {
                StoreUpdateDTO request = createStoreUpdateRequest("Carrefour Express", null, null);

                StoreResponseDTO expected = createStoreResponse(1L, "Carrefour Express", "Olsztyn", "ul. Barcza 4");
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockPutRequest("/api/admin/store/{id}", request, 1L),
                        HttpStatus.NO_CONTENT,
                        expectedJson);
                StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Update store: CITY")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateStoreCityTest() {
                StoreUpdateDTO request = createStoreUpdateRequest(null, "Gdynia", null);

                StoreResponseDTO expected = createStoreResponse(7L, "Tesco", "Gdynia", "ul. Morska 22");
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockPutRequest("/api/admin/store/{id}", request, 7L),
                        HttpStatus.NO_CONTENT,
                        expectedJson);
                StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Update store: STREET")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateStoreStreetTest() {
                StoreUpdateDTO request = createStoreUpdateRequest(null, null, "ul. Zeromskiego 4");

                StoreResponseDTO expected = createStoreResponse(4L, "ABC", "Warszawa", "ul. Zeromskiego 4");
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockPutRequest("/api/admin/store/{id}", request, 4L),
                        HttpStatus.NO_CONTENT,
                        expectedJson);
                StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Invalid update store: UNAUTHORIZED")
            public void updateStoreUnauthorizedTest() {
                StoreUpdateDTO request = createStoreUpdateRequest("Lubi", null, null);
                var putResponse = putRequestAuth("user", "user", "/api/admin/store/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/store/4"
                );
            }
        }
    }

    @Nested
    class BeerPriceAdminTests {

        @Nested
        class GetRequests {

            @Test
            @DisplayName("Get all stored beer prices in array")
            @WithUserDetails("admin")
            public void getBeerPricesAllArrayTest() {
                List<BeerPriceResponseDTO> expected = new ArrayList<>();
                for (Store store : stores) {
                    for (BeerPrice beerPrice : store.getPrices()) {
                        expected.add(new BeerPriceResponseDTO(beerPrice));
                    }
                }
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockGetRequest("/api/admin/beer-price"),
                        HttpStatus.OK, expectedJson);
                List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Get all beer prices in array: UNAUTHORIZED")
            public void getBeerPriceAllArrayUnauthorizedTest() {
                var getResponse = getRequest("/api/admin/beer-price");

                String json = getResponse.getBody();

                assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer-price");
            }
        }
    }
}
