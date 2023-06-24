package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.TestUtils;
import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static com.demo.alkolicznik.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private List<Beer> beers;

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
            ResponseEntity<String> getResponse = requestBeer(1L);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            BeerResponseDTO actualDto = toDTO(jsonResponse);

            BeerResponseDTO expectedDto = new BeerResponseDTO(1L, "Perla Chmielowa Pils", 0.5);
            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        @DisplayName("Error: Get beer of invalid id")
        public void getBeerNotExistingStatusCheckTest() throws Exception {
            ResponseEntity<String> getResponse = requestBeer(9999L);

            String jsonResponse = getResponse.getBody();
            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of 9999 id",
                    "/api/beer/9999");
        }

        @Test
        @DisplayName("Get all stored beers in array")
        public void getBeerArrayTest() {
            ResponseEntity<String> getResponse = requestAllBeers();
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String jsonResponse = getResponse.getBody();
            List<BeerResponseDTO> actual = toDTOList(jsonResponse);

            List<BeerResponseDTO> expected = listToDTOList(beers);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("Create and get valid beer: BRAND")
        @DirtiesContext
        public void createBeerTest() {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String json = postResponse.getBody();
            BeerResponseDTO actual = toDTO(json);

            BeerResponseDTO expected = createBeerResponse(7L, "Lech", 0.5);
            assertThat(actual).isEqualTo(expected);

            // Fetch the newly-created beer.
            ResponseEntity<String> getResponse = requestBeer(7L);

            json = getResponse.getBody();
            actual = toDTO(json);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, VOLUME")
        @DirtiesContext
        public void createBeerWithCustomVolumeTest() {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String json = postResponse.getBody();
            BeerResponseDTO actual = toDTO(json);

            BeerResponseDTO expected = createBeerResponse(7L, "Karmi", 0.6);
            assertThat(actual).isEqualTo(expected);

            // Fetch the newly-created beer.
            ResponseEntity<String> getResponse = requestBeer(7L);

            json = getResponse.getBody();
            actual = toDTO(json);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, TYPE")
        @DirtiesContext
        public void createBeerWithTypePresentTest() {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String json = postResponse.getBody();
            BeerResponseDTO actual = toDTO(json);

            BeerResponseDTO expected = createBeerResponse(7L, "Ksiazece Wisnia", 0.5);
            assertThat(actual).isEqualTo(expected);

            // Fetch the newly-created beer.
            ResponseEntity<String> getResponse = requestBeer(7L);

            json = getResponse.getBody();
            actual = toDTO(json);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create and get valid beer: BRAND, TYPE, VOLUME")
        @DirtiesContext
        public void createBeerWithCustomVolumeAndTypePresentTest() {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String json = postResponse.getBody();
            BeerResponseDTO actual = toDTO(json);

            BeerResponseDTO expected = createBeerResponse(7L, "Zywiec Jasne", 0.33);
            assertThat(actual).isEqualTo(expected);

            // Fetch the newly-created beer.
            ResponseEntity<String> getResponse = requestBeer(7L);

            json = getResponse.getBody();
            actual = toDTO(json);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create and get valid beer: ALREADY_EXISTS but different VOLUME")
        @DirtiesContext
        public void createBeerAlreadyPresentButWithDifferentVolumeTest() {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Perla", "Chmielowa Pils", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String json = postResponse.getBody();
            BeerResponseDTO actual = toDTO(json);

            BeerResponseDTO expected = createBeerResponse(7L, "Perla Chmielowa Pils", 0.33);
            assertThat(actual).isEqualTo(expected);

            // Fetch the newly-created beer.
            ResponseEntity<String> getResponse = requestBeer(7L);

            json = getResponse.getBody();
            actual = toDTO(json);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("Create invalid beer: TYPE present but blank")
        @DirtiesContext
        public void createBeerWithPresentButBlankTypeStatusCheckTest() throws Exception {
            ResponseEntity<String> postResponse = postBeer(createBeerRequest("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: VOLUME negative and equal to zero")
        @DirtiesContext
        public void createBeerWithNegativeVolumeTest() throws Exception {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Pilsner Urquell", null, -0.5)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");

            postResponse = postBeer(
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
        @DirtiesContext
        public void createBeerWithNoBrandTest() throws Exception {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest(null, "Jasne Okocimskie", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }


        @Test
        @DisplayName("Create invalid beer: VOLUME as string")
        @DirtiesContext
        public void createBeerWithVolumeAsStringTest() throws Exception {
            ResponseEntity<String> postResponse = sendRequest(
                    Map.of("brand", "Karpackie",
                            "volume", "half_liter")
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");
        }

        @Test
        @DisplayName("Create invalid beer: BRAND blank")
        @DirtiesContext
        public void createBeerWithBlankBrandTest() throws Exception {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest(" \t \t  \t\t ", "Cerny", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");

            postResponse = postBeer(
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
        @DirtiesContext
        public void createBeerWithBlankType() throws Exception {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Miloslaw", "  \t\t ", 0.6)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");

            postResponse = postBeer(
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
        @DirtiesContext
        public void createBeerAlreadyPresentTest() throws Exception {
            ResponseEntity<String> postResponse = postBeer(
                    createBeerRequest("Perla", "Chmielowa Pils", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer already exists",
                    "/api/beer");

            postResponse = postBeer(
                    createBeerRequest("Zubr", null, null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Beer already exists",
                    "/api/beer");
        }
    }
}
