package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.delete.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.responses.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.dto.responses.StoreResponseDTO;
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
import java.util.Map;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.ResponseTestUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@AutoConfigureMockMvc
public class AdminTests {

    @Autowired
    private List<Store> stores;

    @Autowired
    private List<Beer> beers;

    public static MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        AdminTests.mockMvc = mockMvc;
    }

    @Nested
    class BeerTests {

        @Nested
        class GetRequests {

            @Test
            @DisplayName("Get all stored beers in array")
            @WithUserDetails("admin")
            public void getBeerAllArrayAuthorizedTest() {
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

                BeerResponseDTO expected = createBeerResponse(3L, "Tyskie", "Gronie", 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 3L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/{id}", 3L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Update beer: BRAND")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerBrandTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);

                BeerResponseDTO expected = createBeerResponse(3L, "Ksiazece", "Gronie", 0.6);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 3L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/{id}", 3L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Update beer: TYPE")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerTypeTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "IPA", null);

                BeerResponseDTO expected = createBeerResponse(2L, "Ksiazece", "IPA", 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 2L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/{id}", 2L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
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

            @Test
            @DisplayName("Update beer: remove TYPE")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerSetTypeToNullTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "", null);

                BeerResponseDTO expected = createBeerResponse(6L, "Miloslaw", null, 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 6L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/{id}", 6L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Invalid update beer: PROPERTIES_SAME (2)")
            public void updateBeerUnchangedTwoTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Zubr", null, 0.5);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/beer/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/admin/beer/4"
                );
            }

            @Test
            @DisplayName("Update beer: previously TYPE null")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerWithTypeNullTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Zubr", "Ciemnozloty", 0.5);

                BeerResponseDTO expected = createBeerResponse(4L, "Zubr", "Ciemnozloty", 0.5);
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/admin/beer/{id}", request, 4L),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/{id}", 4L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }
        }

        @Nested
        class DeleteRequests {

            @Test
            @DisplayName("Delete beer")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteBeerTest() {
                BeerDeleteDTO expected = createBeerDeleteResponse(
                        getBeer(6L, beers),
                        "Beer was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockDeleteRequest("/api/admin/beer/{id}", 6L),
                        HttpStatus.OK,
                        expectedJson);
                assertThat(actualJson).isEqualTo(expectedJson);

                var getRequest = getRequest("/api/beer/{id}", 6);

                String jsonResponse = getRequest.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '6' id",
                        "/api/beer/6");
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

                var getResponse = getRequest("/api/store/{id}", 1L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
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

                var getResponse = getRequest("/api/store/{id}", 7L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
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

                var getResponse = getRequest("/api/store/{id}", 4L);

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
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

            @Test
            @DisplayName("Invalid update store: NAME blank")
            public void updateStoreNameBlankTest() {
                StoreUpdateDTO request = createStoreUpdateRequest("", null, null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Name was not specified",
                        "/api/admin/store/4"
                );

                request = createStoreUpdateRequest("\t\n ", null, null);
                putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Name was not specified",
                        "/api/admin/store/4"
                );
            }

            @Test
            @DisplayName("Invalid update store: SAME_PROPERTIES")
            public void updateStorePropertiesSameTest() {
                StoreUpdateDTO request = createStoreUpdateRequest("Lubi", "Warszawa", "ul. Nowaka 5");
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/admin/store/{id}", request, 5L);

                String jsonResponse = putResponse.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/admin/store/5");
            }

            @Test
            @DisplayName("Invalid update store: STREET blank")
            public void updateStoreStreetBlankTest() {
                StoreUpdateDTO request = createStoreUpdateRequest(null, null, "");
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Street was not specified",
                        "/api/admin/store/4"
                );

                request = createStoreUpdateRequest(null, null, "\t\n ");
                putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Street was not specified",
                        "/api/admin/store/4"
                );
            }

            @Test
            @DisplayName("Invalid update store: CITY blank")
            public void updateStoreCityBlankTest() {
                StoreUpdateDTO request = createStoreUpdateRequest(null, "", null);
                var putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "City was not specified",
                        "/api/admin/store/4"
                );

                request = createStoreUpdateRequest(null, "\t\n ", null);
                putResponse = putRequestAuth("admin", "admin", "/api/admin/store/{id}", request, 4L);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "City was not specified",
                        "/api/admin/store/4"
                );
            }
        }

        @Nested
        class DeleteRequests {

            @Test
            @DisplayName("Delete store")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteStoreTest() {
                StoreDeleteDTO expected = createStoreDeleteResponse(
                        getStore(6L, stores),
                        "Store was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockDeleteRequest("/api/admin/store/{id}", 6L),
                        HttpStatus.OK,
                        expectedJson);
                assertThat(actualJson).isEqualTo(expectedJson);

                var getRequest = getRequest("/api/store/{id}", 6);

                String jsonResponse = getRequest.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '6' id",
                        "/api/store/6");
            }

            @Test
            @DisplayName("Invalid delete store: UNAUTHORIZED")
            public void deleteStoreUnauthorizedTest() {
                var deleteResponse = deleteRequestAuth("user", "user",
                        "/api/admin/store/{id}", 2L);

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/store/2"
                );
            }

            @Test
            @DisplayName("Invalid delete store: STORE_NOT_EXISTS")
            public void deleteStoreNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/admin/store/{id}", 0L);

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '0' id",
                        "/api/admin/store/0"
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

        @Nested
        class PutRequests {

            @Test
            @DisplayName("Update beer price: PRICE")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerPricePriceTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(4.59);

                BeerPriceResponseDTO expected = createBeerPriceResponse(
                        createBeerResponse(getBeer(3L, beers)),
                        createStoreResponse(getStore(3L, stores)),
                        "PLN 4.59"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockPutRequest(
                                "/api/admin/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L)
                        ),
                        HttpStatus.NO_CONTENT,
                        expectedJson);
                BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer-price", Map.of("beer_id", 3L, "store_id", 3L));

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerPriceResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Invalid update beer price: PRICE negative and zero")
            public void updateBeerPricePriceNegativeAndZeroTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(0d);
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/admin/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                String jsonResponse = putResponse.getBody();
                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Price must be a positive number",
                        "/api/admin/beer-price");

                request = createBeerPriceUpdateRequest(-5.9);
                putResponse = putRequestAuth("admin", "admin",
                        "/api/admin/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                jsonResponse = putResponse.getBody();
                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Price must be a positive number",
                        "/api/admin/beer-price");
            }

            @Test
            @DisplayName("Invalid update beer price: PRICE missing")
            public void updateBeerPricePriceNullTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(null);
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/admin/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                String jsonResponse = putResponse.getBody();
                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "No property to update was specified",
                        "/api/admin/beer-price");
            }

            @Test
            @DisplayName("Invalid update beer price: SAME_PROPERTIES")
            public void updateBeerPricePropertiesSameTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(2.89);
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/admin/beer-price", request, Map.of("beer_id", 4L, "store_id", 2L));

                String jsonResponse = putResponse.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/admin/beer-price");
            }

            @Test
            @DisplayName("Invalid update beer price: UNAUTHORIZED")
            public void updateBeerPricePriceUnauthorizedTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(7.89);
                var putResponse = putRequestAuth("user", "user",
                        "/api/admin/beer-price", request, Map.of("beer_id", 3L, "store_id", 2L));

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/beer-price"
                );
            }
        }

        @Nested
        class DeleteRequests {

            @Test
            @DisplayName("Delete beer price")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteBeerPriceTest() {
                BeerPriceDeleteDTO expected = createBeerPriceDeleteResponse(
                        getBeer(2L, beers),
                        getStore(5L, stores),
                        "5.49 PLN",
                        "Beer price was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockDeleteRequest("/api/admin/beer-price",
                                Map.of("beer_id", 2L, "store_id", 5L)),
                        HttpStatus.OK,
                        expectedJson);
                assertThat(actualJson).isEqualTo(expectedJson);

                var getRequest = getRequest("/api/beer-price", Map.of("beer_id", 2L, "store_id", 5L));

                String jsonResponse = getRequest.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Store does not currently sell this beer",
                        "/api/beer-price");
            }

            @Test
            @DisplayName("Invalid delete beer price: UNAUTHORIZED")
            public void deleteBeerPriceUnauthorizedTest() {
                var deleteResponse = deleteRequestAuth("user", "user",
                        "/api/admin/beer-price", Map.of("beer_id", 2L, "store_id", 5L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "/api/admin/beer-price"
                );
            }

            @Test
            @DisplayName("Invalid delete beer price: STORE_NOT_EXISTS")
            public void deleteBeerPriceStoreNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/admin/beer-price", Map.of("store_id", 913L, "beer_id", 3L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '913' id",
                        "/api/admin/beer-price"
                );
            }

            @Test
            @DisplayName("Invalid delete beer price: BEER_NOT_EXISTS")
            public void deleteBeerPriceBeerNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/admin/beer-price", Map.of("store_id", 3L, "beer_id", 433L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '433' id",
                        "/api/admin/beer-price"
                );
            }

            @Test
            @DisplayName("Invalid delete beer price: BEER_PRICE_NOT_EXISTS")
            public void deleteBeerPricePriceNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/admin/beer-price", Map.of("store_id", 5L, "beer_id", 1L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Store does not currently sell this beer",
                        "/api/admin/beer-price"
                );
            }
        }
    }
}