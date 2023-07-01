package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
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
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerApiTests {

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

    /**
     * Launch this test to see whether the
     * ApplicationContext loads correctly.
     */
    @Test
    public void contextLoads() {
    }

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get beer of valid id")
        public void getBeerTest() {
            var getResponse = getRequest("/api/beer/{id}", 1L);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = new BeerResponseDTO(1L, "Perla Chmielowa Pils", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Error: Get beer of invalid id")
        public void getBeerNotExistingStatusCheckTest() throws Exception {
            var getResponse = getRequest("/api/beer/{id}", 9999L);

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of 9999 id",
                    "/api/beer/9999");
        }

        @Test
        @DisplayName("Get all stored beers in array w/ authorization")
        public void getBeerAllArrayAuthorizedTest() {
            var getResponse = getRequestWithBasicAuth("/api/admin/beer", "admin", "admin");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            List<BeerResponseDTO> actual = toBeerResponseDTOList(jsonResponse);

            List<BeerResponseDTO> expected = beers.stream()
                    .map(BeerResponseDTO::new)
                    .collect(Collectors.toList());
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Get all beers in array of city")
        public void getBeerFromCityArrayTest() {
            var getResponse = getRequest("/api/beer", Map.of("city", "Olsztyn"));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            System.out.println(jsonResponse);
            List<BeerResponseDTO> actual = toBeerResponseDTOList(jsonResponse);

            List<BeerResponseDTO> expected = new ArrayList<>();
            stores.stream()
                    .filter((store -> store.getCity().equals("Olsztyn")))
                    .map(Store::getPrices)
                    .forEach(beerPrices -> beerPrices.stream()
                            .map(BeerPrice::getBeer)
                            .map(BeerResponseDTO::new)
                            .forEach(beerResponseDTO -> expected.add(beerResponseDTO)));
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Get all beers in array w/o authorization")
        public void getBeerAllArrayUnauthorizedTest() throws Exception {
            var getResponse = getRequest("/api/admin/beer");

            String json = getResponse.getBody();

            assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/admin/beer");
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("Create and get valid beer: BRAND")
        @DirtiesContext
        public void createBeerTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponseDTO(7L, "Lech", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/{id}", 7L);

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, VOLUME")
        @DirtiesContext
        public void createBeerWithCustomVolumeTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponseDTO(7L, "Karmi", 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/{id}", 7L);

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, TYPE")
        @DirtiesContext
        public void createBeerWithTypePresentTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponseDTO(7L, "Ksiazece Wisnia", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/{id}", 7L);

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, TYPE, VOLUME")
        @DirtiesContext
        public void createBeerWithCustomVolumeAndTypePresentTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponseDTO(7L, "Zywiec Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/{id}", 7L);

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Create and get valid beer: ALREADY_EXISTS but different VOLUME")
        @DirtiesContext
        public void createBeerAlreadyPresentButWithDifferentVolumeTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Perla", "Chmielowa Pils", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponseDTO(7L, "Perla Chmielowa Pils", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/{id}", 7L);

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("Create invalid beer: TYPE present but blank")
       // @DirtiesContext
        public void createBeerWithPresentButBlankTypeStatusCheckTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: VOLUME negative and equal to zero")
       // @DirtiesContext
        public void createBeerWithNegativeVolumeTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Pilsner Urquell", null, -0.5)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Lomza", null, 0d)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: BRAND null")
       // @DirtiesContext
        public void createBeerWithNoBrandTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO(null, "Jasne Okocimskie", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: BRAND blank")
       // @DirtiesContext
        public void createBeerWithBlankBrandTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO(" \t \t  \t\t ", "Cerny", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("", "Cerny", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: TYPE blank")
       // @DirtiesContext
        public void createBeerWithBlankType() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Miloslaw", "  \t\t ", 0.6)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Miloslaw", "", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: ALREADY_EXISTS")
       // @DirtiesContext
        public void createBeerAlreadyPresentTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Perla", "Chmielowa Pils", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer already exists",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequestDTO("Zubr", null, null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer already exists",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: BRAND null, TYPE blank, VOLUME negative")
       // @DirtiesContext
        public void createBeerBrandNullTypeBlankVolumeNegativeTest() throws Exception {
            var postResponse = postRequest("/api/beer",
                    createBeerRequestDTO(null, " \t", -15.9)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified; Type was not specified; Volume must be a positive number",
                    "/api/beer");
        }
    }
}
