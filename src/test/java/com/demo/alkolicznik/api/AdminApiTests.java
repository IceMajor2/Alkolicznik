package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
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
    class GetRequests {

        @Test
        @DisplayName("Get all stores w/ authorization")
        @WithUserDetails("admin")
        public void getStoresAllAuthorizedTest() {
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
        @DisplayName("Get all stored beers in array w/ authorization")
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
        @DisplayName("Get all stored beer prices in array w/ authorization")
        @WithUserDetails("admin")
        public void getBeerPricesAllArrayAuthorizedTest() throws Exception {
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
        @DisplayName("Get all stores w/o authorization")
        public void getStoresAllUnauthorizedTest() {
            var getResponse = getRequest("/api/admin/store");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/store");
        }

        @Test
        @DisplayName("Get all beers in array w/o authorization")
        public void getBeerAllArrayUnauthorizedTest() {
            var getResponse = getRequest("/api/admin/beer");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer");
        }

        @Test
        @DisplayName("Get all beer prices in array w/o authorization")
        public void getBeerPriceAllArrayUnauthorizedTest() {
            var getResponse = getRequest("/api/admin/beer-price");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer-price");
        }
    }

    @Nested
    class PutRequests {

        @Test
        @DisplayName("Update beer: VOLUME")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerVolumeTest() throws Exception {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.5);

            BeerResponseDTO expected = new BeerResponseDTO(3L, "Tyskie Gronie", 0.5);
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
        public void updateBeerBrandTest() throws Exception {
            BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);

            BeerResponseDTO expected = new BeerResponseDTO(3L, "Ksiazece Gronie", 0.6);
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
        public void updateBeerTypeTest() throws Exception {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "IPA", null);

            BeerResponseDTO expected = new BeerResponseDTO(2L, "Ksiazece IPA", 0.5);
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
        @WithUserDetails("admin")
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
    }
}
