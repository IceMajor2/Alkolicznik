package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
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

import static com.demo.alkolicznik.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerApiTests {

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

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
        @DisplayName("Get beer of invalid id")
        public void getBeerNotExistingStatusCheckTest() {
            var getResponse = getRequest("/api/beer/{id}", 9999L);

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '9999' id",
                    "/api/beer/9999");
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("Create and get valid beer: BRAND")
        @DirtiesContext
        public void createBeerTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Lech", 0.5);
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
                    createBeerRequest("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Karmi", 0.6);
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
                    createBeerRequest("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Ksiazece Wisnia", 0.5);
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
                    createBeerRequest("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Zywiec Jasne", 0.33);
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
                    createBeerRequest("Perla", "Chmielowa Pils", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Perla Chmielowa Pils", 0.33);
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
        public void createBeerWithPresentButBlankTypeStatusCheckTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: VOLUME negative and equal to zero")
        // @DirtiesContext
        public void createBeerWithNegativeVolumeTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest("Pilsner Urquell", null, -0.5)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequest("Lomza", null, 0d)
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
        public void createBeerWithNoBrandTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest(null, "Jasne Okocimskie", null)
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
        public void createBeerWithBlankBrandTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest(" \t \t  \t\t ", "Cerny", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequest("", "Cerny", null)
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
        public void createBeerWithBlankType() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest("Miloslaw", "  \t\t ", 0.6)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequest("Miloslaw", "", null)
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
        public void createBeerAlreadyPresentTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");

            postResponse = postRequest("/api/beer",
                    createBeerRequest("Zubr", null, null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: BRAND null, TYPE blank, VOLUME negative")
        // @DirtiesContext
        public void createBeerBrandNullTypeBlankVolumeNegativeTest() {
            var postResponse = postRequest("/api/beer",
                    createBeerRequest(null, " \t", -15.9)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified; Type was not specified; Volume must be a positive number",
                    "/api/beer");
        }
    }
}
