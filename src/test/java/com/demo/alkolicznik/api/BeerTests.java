package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
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

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerTests {

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

    @Nested
    class GetRequests {

        @Test
        @DisplayName("GET: '/api/beer/{beer_id}")
        public void getBeerTest() {
            var getResponse = getRequest("/api/beer/1");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(getBeer(1L, beers));
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("GET: '/api/beer/{beer_id} [BEER_NOT_FOUND]")
        public void getBeerNotExistingStatusCheckTest() {
            var getResponse = getRequest("/api/beer/9999");

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
        @DisplayName("POST: '/api/beer'")
        @DirtiesContext
        public void createBeerTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Lech", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and volume only")
        @DirtiesContext
        public void createBeerWithCustomVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Karmi", null, 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and type only")
        @DirtiesContext
        public void createBeerWithTypePresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Ksiazece", "Wisnia", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' everything specified")
        @DirtiesContext
        public void createBeerWithCustomVolumeAndTypePresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Zywiec", "Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand & type existing, volume unique")
        @DirtiesContext
        public void createBeerAlreadyPresentButWithDifferentVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Perla", "Chmielowa Pils", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' [TYPE_BLANK]")
        // @DirtiesContext
        public void createBeerWithPresentButBlankTypeStatusCheckTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [VOLUME_NON_POSITIVE]")
        // @DirtiesContext
        public void createBeerWithNegativeVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Pilsner Urquell", null, -0.5)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Lomza", null, 0d)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_NULL]")
        // @DirtiesContext
        public void createBeerWithNoBrandTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(null, "Jasne Okocimskie", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_BLANK]")
        // @DirtiesContext
        public void createBeerWithBlankBrandTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(" \t \t  \t\t ", "Cerny", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("", "Cerny", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [TYPE_BLANK]")
        // @DirtiesContext
        public void createBeerWithBlankType() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Miloslaw", "  \t\t ", 0.6)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Miloslaw", "", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BEER_EXISTS]")
        // @DirtiesContext
        public void createBeerAlreadyPresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Zubr", null, null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_NULL; TYPE_BLANK; VOLUME_NEGATIVE]")
        // @DirtiesContext
        public void createBeerBrandNullTypeBlankVolumeNegativeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
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
