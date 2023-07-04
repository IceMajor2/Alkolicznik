package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.dto.StoreResponseDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.demo.alkolicznik.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@AutoConfigureMockMvc
public class AdminApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private List<Store> stores;

    @Autowired
    private List<Beer> beers;

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get all stores w/ authorization")
        @WithUserDetails("admin")
        public void getStoresAllAuthorizedTest() throws Exception {
            List<StoreResponseDTO> expected = stores.stream()
                    .map(StoreResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);

            String actualJson = mockMvc.perform(get("/api/admin/store"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString();
            List<StoreResponseDTO> actual = toModelList(actualJson, StoreResponseDTO.class);

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

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer-price");
        }

        @Test
        @DisplayName("Get all beer prices in array w/o authorization")
        public void getBeerPriceAllArrayUnauthorizedTest() {
            var getResponse = getRequest("/api/admin/beer-price");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer");
        }

        @Test
        @DisplayName("Get all stored beers in array w/ authorization")
        @WithUserDetails("admin")
        public void getBeerAllArrayAuthorizedTest() throws Exception {
            List<BeerResponseDTO> expected = beers.stream()
                    .map(BeerResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);

            String actualJson = mockMvc.perform(get("/api/admin/beer"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString();
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

            mockMvc.perform(get("/api/admin/beer-price"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));
        }
    }
}