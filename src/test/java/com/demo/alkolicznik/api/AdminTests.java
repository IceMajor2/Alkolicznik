package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.delete.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.delete.StoreDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.put.StoreUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
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
import static com.demo.alkolicznik.utils.TestUtils.*;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.*;
import static com.demo.alkolicznik.utils.requests.MockRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.*;
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

                String actualJson = assertMockRequest(mockGetRequest("/api/beer"),
                        HttpStatus.OK,
                        expectedJson);
                List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
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

                BeerResponseDTO expected = createBeerResponse(
                        3L, "Tyskie", "Gronie", 0.5, getImage(3L, beers));
                String expectedJson = toJsonString(expected);
                System.out.println(expectedJson);
                String actualJson = assertMockRequest(
                        mockPutRequest("/api/beer/3", request),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/3");

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

                BeerResponseDTO expected = createBeerResponse(3L, "Ksiazece", "Gronie", 0.65, getImage(3L, beers));
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/beer/3", request),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/3");

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
                        mockPutRequest("/api/beer/2", request),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/2");

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Invalid beer update: request EMPTY")
            public void updateBeerEmptyRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, null);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/6", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "No property to update was specified",
                        "/api/beer/6"
                );
            }

            @Test
            @DisplayName("Invalid beer update: VOLUME negative and zero")
            public void updateBeerVolumeNegativeAndZeroRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0d);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Volume must be a positive number",
                        "/api/beer/4"
                );

                request = createBeerUpdateRequest(null, null, -5.1d);
                putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Volume must be a positive number",
                        "/api/beer/4"
                );
            }

            @Test
            @DisplayName("Invalid beer update: BEER_NOT_EXISTS")
            public void updateBeerNotExistsRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/321", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '321' id",
                        "/api/beer/321"
                );
            }

            @Test
            @DisplayName("Invalid beer update: BRAND blank")
            public void updateBeerBrandBlankRequestTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("\t \t \n\n\n", null, null);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Brand was not specified",
                        "/api/beer/5"
                );

                request = createBeerUpdateRequest("", null, null);
                putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

                jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Brand was not specified",
                        "/api/beer/5"
                );
            }

            @Test
            @DisplayName("Invalid beer update: PROPERTIES_SAME")
            public void updateBeerUnchangedTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Komes", "Porter Malinowy", 0.33);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/beer/5"
                );
            }

            @Test
            @DisplayName("Update beer: remove TYPE")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerSetTypeToNullTest() {
                BeerUpdateDTO request = createBeerUpdateRequest(null, "", null);

                BeerResponseDTO expected = createBeerResponse(6L, "Miloslaw", null, 0.5, getImage(6L, beers));
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/beer/6", request),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/6");

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }

            @Test
            @DisplayName("Invalid update beer: PROPERTIES_SAME (2)")
            public void updateBeerUnchangedTwoTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Zubr", null, 0.5);
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

                String jsonResponse = putResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/beer/4"
                );
            }

            @Test
            @DisplayName("Update beer: previously TYPE null")
            @DirtiesContext
            @WithUserDetails("admin")
            public void updateBeerWithTypeNullTest() {
                BeerUpdateDTO request = createBeerUpdateRequest("Zubr", "Ciemnozloty", 0.5);

                BeerResponseDTO expected = createBeerResponse(4L, "Zubr", "Ciemnozloty", 0.5, getImage(4L, beers));
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(
                        mockPutRequest("/api/beer/4", request),
                        HttpStatus.NO_CONTENT,
                        expectedJson
                );
                BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);

                var getResponse = getRequest("/api/beer/4");

                actualJson = getResponse.getBody();
                actual = toModel(actualJson, BeerResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
            }
        }

        @Nested
        class DeleteRequests {

            @Test
            @DisplayName("Delete beer by id")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteBeerByIdTest() {
                BeerDeleteDTO expected = createBeerDeleteResponse(
                        getBeer(6L, beers),
                        "Beer was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                String actualJson = assertMockRequest(mockDeleteRequest("/api/beer/6"),
                        HttpStatus.OK,
                        expectedJson);
                assertThat(actualJson).isEqualTo(expectedJson);

                var getRequest = getRequest("/api/beer/6");

                String jsonResponse = getRequest.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '6' id",
                        "/api/beer/6");
            }

            @Test
            @DisplayName("Delete beer by properties (except id)")
            @DirtiesContext
            @WithUserDetails("admin")
            public void deleteBeerByPropertiesTest() {
                BeerDeleteDTO expected = createBeerDeleteResponse(
                        getBeer(3L, beers),
                        "Beer was deleted successfully!"
                );
                String expectedJson = toJsonString(expected);

                BeerRequestDTO requestDTO = createBeerRequest(getBeer(3L, beers));
                String actualJson = assertMockRequest(
                        mockDeleteRequest(requestDTO, "/api/beer"),
                        HttpStatus.OK,
                        expectedJson
                );

                BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);
                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Invalid delete beer: BEER_NOT_EXISTS")
            public void deleteBeerNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/beer/0");

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '0' id",
                        "/api/beer/0"
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

                String actualJson = assertMockRequest(mockGetRequest("/api/store"),
                        HttpStatus.OK, expectedJson);
                List<StoreResponseDTO> actual = toModelList(actualJson, StoreResponseDTO.class);

                assertThat(actual).isEqualTo(expected);
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
            @DisplayName("Update store: CITY")
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
            @DisplayName("Update store: STREET")
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
            @DisplayName("Invalid update store: NAME blank")
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
            @DisplayName("Invalid update store: SAME_PROPERTIES")
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
            @DisplayName("Invalid update store: STREET blank")
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
            @DisplayName("Invalid update store: CITY blank")
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
            @DisplayName("Invalid delete store: STORE_NOT_EXISTS")
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

                String actualJson = assertMockRequest(mockGetRequest("/api/beer-price"),
                        HttpStatus.OK, expectedJson);
                List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

                assertThat(actual).hasSameElementsAs(expected);
                assertThat(actual).isEqualTo(expected);
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
                                "/api/beer-price", Map.of("beer_id", 3L, "store_id", 3L), request
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
                        "/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                String jsonResponse = putResponse.getBody();
                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Price must be a positive number",
                        "/api/beer-price");

                request = createBeerPriceUpdateRequest(-5.9);
                putResponse = putRequestAuth("admin", "admin",
                        "/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                jsonResponse = putResponse.getBody();
                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "Price must be a positive number",
                        "/api/beer-price");
            }

            @Test
            @DisplayName("Invalid update beer price: PRICE missing")
            public void updateBeerPricePriceNullTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(null);
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

                String jsonResponse = putResponse.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.BAD_REQUEST,
                        "No property to update was specified",
                        "/api/beer-price");
            }

            @Test
            @DisplayName("Invalid update beer price: SAME_PROPERTIES")
            public void updateBeerPricePropertiesSameTest() {
                BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(2.89);
                var putResponse = putRequestAuth("admin", "admin",
                        "/api/beer-price", request, Map.of("beer_id", 4L, "store_id", 2L));

                String jsonResponse = putResponse.getBody();

                assertIsError(jsonResponse,
                        HttpStatus.OK,
                        "Objects are the same: nothing to update",
                        "/api/beer-price");
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

                String actualJson = assertMockRequest(mockDeleteRequest("/api/beer-price",
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
            @DisplayName("Invalid delete beer price: STORE_NOT_EXISTS")
            public void deleteBeerPriceStoreNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/beer-price", Map.of("store_id", 913L, "beer_id", 3L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '913' id",
                        "/api/beer-price"
                );
            }

            @Test
            @DisplayName("Invalid delete beer price: BEER_NOT_EXISTS")
            public void deleteBeerPriceBeerNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/beer-price", Map.of("store_id", 3L, "beer_id", 433L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '433' id",
                        "/api/beer-price"
                );
            }

            @Test
            @DisplayName("Invalid delete beer price: BEER_PRICE_NOT_EXISTS")
            public void deleteBeerPricePriceNotExistsTest() {
                var deleteResponse = deleteRequestAuth("admin", "admin",
                        "/api/beer-price", Map.of("store_id", 5L, "beer_id", 1L));

                String jsonResponse = deleteResponse.getBody();

                assertIsError(
                        jsonResponse,
                        HttpStatus.NOT_FOUND,
                        "Store does not currently sell this beer",
                        "/api/beer-price"
                );
            }
        }
    }
}
