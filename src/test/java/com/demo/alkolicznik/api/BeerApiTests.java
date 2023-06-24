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

            String jsonResponse = getResponse.getBody();
            BeerResponseDTO actualDto = toDTO(jsonResponse);

            BeerResponseDTO expectedDto = new BeerResponseDTO(1L, "Perla Chmielowa Pils", 0.5);
            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        @DisplayName("Return 200 when valid get beer request")
        public void getBeerStatusCheckTest() {
            ResponseEntity<String> getResponse = requestBeer(5L);

            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Return 404 when invalid id on get beer request")
        public void getBeerNotExistingStatusCheckTest() throws Exception {
            ResponseEntity<String> getResponse = requestBeer(9999L);

            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Get beer of invalid id: check error body")
        public void getBeerNotExistingErrorBodyTest() throws JSONException {
            ResponseEntity<String> getResponse = requestBeer(9999L);

            String jsonResponse = getResponse.getBody();
            JSONObject actual = getJsonObject(jsonResponse);

            assertIsError(actual,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of 9999 id",
                    "/api/beer/9999");
        }

        @Test
        @DisplayName("Return 200 on get beer array request")
        public void getBeerArrayStatusCheckTest() {
            ResponseEntity<String> getResponse = requestAllBeers();

            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Get all stored beers in array")
        public void getBeerArrayTest() {
            ResponseEntity<String> getResponse = requestAllBeers();

            String jsonResponse = getResponse.getBody();
            List<BeerResponseDTO> actual = convertJsonArrayToList(jsonResponse);

            List<BeerResponseDTO> expected = listToDTOList(beers);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class PostRequests {
        @Test
        @DisplayName("Return 400 when invalid 'type' field on POST request")
        @DirtiesContext
        public void createBeerWithPresentButBlankTypeStatusCheckTest() {
            ResponseEntity<String> postResponse = postBeer(createBeerRequest("Heineken", " ", null));

            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


        /**
         * {@code POST /api/beer} - valid beer with default {@code volume} and empty {@code type} fields.
         */
        @Test
        @DirtiesContext
        public void createBeerTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Lech");

            BeerResponseDTO expected = new BeerResponseDTO();
            expected.setId(7L);
            expected.setFullName("Lech");
            expected.setVolume(0.5d);

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
        }

        /**
         * {@code POST /api/beer} - valid beer with non-default {@code volume} field.
         */
        @Test
        @DirtiesContext
        public void createBeerWithCustomVolumeTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Lomza");
            request.setVolume(0.6);

            BeerResponseDTO expected = new BeerResponseDTO();
            expected.setFullName("Lomza");
            expected.setVolume(0.6);
            expected.setId(7L);

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
        }

        /**
         * {@code POST /api/beer} - valid beer with specified {@code type} field.
         */
        @Test
        @DirtiesContext
        public void createBeerWithTypePresentTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Okocim");
            request.setType("Jasne Okocimskie");

            BeerResponseDTO expected = new BeerResponseDTO();
            expected.setFullName("Okocim Jasne Okocimskie");
            expected.setVolume(0.5);
            expected.setId(7L);

            // TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
        }

        /**
         * {@code POST /api/beer} - valid beer with non-default {@code volume} field and specified {@code type} field.
         */
        @Test
        @DirtiesContext
        public void createBeerWithCustomVolumeAndTypePresentTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Budweiser");
            request.setType("Budvar Original");
            request.setVolume(0.33);

            BeerResponseDTO expected = new BeerResponseDTO();
            expected.setFullName("Budweiser Budvar Original");
            expected.setVolume(0.33);
            expected.setId(7L);

            // TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
        }

        /**
         * {@code POST /api/beer} - send invalid BeerRequestDTO body (negative {@code volume} field).
         */
        @Test
        @DirtiesContext
        //  @Disabled
        public void createBeerWithNegativeVolumeTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Pilsner Urquell");
            request.setVolume(-1.0);

            // TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST,
            //         request, null);
        }

        @Test
        @DisplayName("Create beer with present but blank 'type' field")
        @DirtiesContext
        public void createBeerWithPresentButBlankTypeTest() {
            ResponseEntity<String> postResponse = postBeer(createBeerRequest("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();
            JSONObject actual = getJsonObject(jsonResponse);

            assertIsError(actual,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        /**
         * {@code POST /api/beer} - send invalid BeerRequestDTO body (no {@code brand} field).
         */
        @Test
        @DirtiesContext
        //  @Disabled
        public void createBeerWithNoBrandTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setVolume(0.6);
            request.setType("IPA");

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);
        }


        /**
         * {@code POST /api/beer} - send invalid BeerRequestDTO body ({@code volume} as a word, not numeric value).
         */
        @Test
        @DirtiesContext
        @Disabled
        public void createBeerWithVolumeAsStringTest() {

        }

        /**
         * {@code POST /api/beer} - send valid BeerRequestDTO body but with untrimmed {@code brand} field.
         * Assert that it is trimmed by accessing at {@code GET /api/beer/{id}}
         */
        @Test
        @DirtiesContext
        // @Disabled
        public void createBeerWithUntrimmedBrandInRequestTest() {

        }

        /**
         * {@code POST /api/beer} - send valid BeerRequestDTO body but with untrimmed {@code type} field.
         * Assert that it is trimmed by accessing at {@code GET /api/beer/{id}}
         */
        @Test
        @DirtiesContext
        @Disabled
        public void createBeerWithUntrimmedTypeInRequestTest() {

        }

        /**
         * {@code POST /api/beer} - send invalid BeerRequestDTO body (empty {@code brand} field).
         */
        @Test
        @DirtiesContext
        // @Disabled
        public void createBeerWithEmptyBrandTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setVolume(0.6);
            request.setType("IPA");
            request.setBrand("");

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);
        }

        /**
         * {@code POST /api/beer} - send valid BeerRequestDTO body but of already-existing beer.
         */
        @Test
        @DirtiesContext
        public void createBeerAlreadyPresentTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Perla");
            request.setType("Chmielowa Pils");

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);

            request.setBrand("Tyskie");
            request.setType("Gronie");
            request.setVolume(0.6);

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);
        }

        /**
         * {@code POST /api/beer} - send valid BeerRequestDTO body with beer of already-existing
         * brand and type, but different volume.
         */
        @Test
        @DirtiesContext
        public void createBeerAlreadyPresentButWithDifferentVolumeTest() {
            BeerRequestDTO request = new BeerRequestDTO();
            request.setBrand("Komes");
            request.setType("Malinowe");

            BeerResponseDTO expected = new BeerResponseDTO();
            expected.setId(7L);
            expected.setVolume(0.5);
            expected.setFullName("Komes Malinowe");

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);

            request.setBrand("Perla");
            request.setType("Chmielowa Pils");
            request.setVolume(0.33);

            expected.setId(8L);
            expected.setVolume(0.33);
            expected.setFullName("Perla Chmielowa Pils");

            //TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
        }

        /**
         * {@code POST /api/beer} - check body of 200 OK response.
         */
        @Test
        @DirtiesContext
        public void createBeerResponseBodyOKTest() throws JSONException {
            Beer beer = new Beer("Okocim");
            ResponseEntity<String> postResponse = restTemplate
                    .postForEntity("/api/beer", beer, String.class);
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            JSONObject jsonObject = TestUtils.getJsonObject(postResponse.getBody());
            String nameResponse = jsonObject.getString("name");
            Long nameId = jsonObject.getLong("id");
            double volume = jsonObject.getDouble("volume");
            int lengthResponse = jsonObject.length();

            assertThat(nameResponse).isEqualTo("Okocim");
            assertThat(nameId).isEqualTo(7L);
            assertThat(volume).isEqualTo(0.5d);
            assertThat(lengthResponse)
                    .withFailMessage("Amount of key-value pairs do not match." +
                                    "\nExpected: %d" +
                                    "\nActual: %d",
                            3, lengthResponse)
                    .isEqualTo(3);
        }
    }
}
