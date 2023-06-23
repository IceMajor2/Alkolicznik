package com.demo.alkolicznik;

import com.demo.alkolicznik.dto.BeerRequestDTO;
import com.demo.alkolicznik.dto.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

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

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Launch this test to see whether the
     * ApplicationContext loads correctly.
     */
    @Test
    public void contextLoads() {
    }

    /**
     * {@code GET /api/beer/{id}} - get beer OK request.
     */
    @Test
    public void getBeerTest() throws Exception {
        ResponseEntity<BeerResponseDTO> response = restTemplate
                .getForEntity("/api/beer/1", BeerResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerResponseDTO actual = response.getBody();

        BeerResponseDTO expected = TestUtils.convertJdbcQueryToDto
                ("SELECT * FROM beer WHERE beer.id = 1",
                        TestUtils.mapToBeer());
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * {@code GET /api/beer/{id}} - check acquiring of non-existing beer (id not found).
     */
    @Test
    public void getNonExistingBeerShouldReturn404Test() {
        ResponseEntity<BeerResponseDTO> getResponse = restTemplate.getForEntity("/api/beer/9999", BeerResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * {@code GET /api/beer} - get array of all beers in database test.
     */
    @Test
    public void getAllBeersTest() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/api/beer", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Compare actual and expected beer names.
        JSONArray beerNames = TestUtils.getValues(response.getBody(), "name");
        String[] beerNamesDb = TestUtils.convertNamesToArray(
                this.beers.stream().map(Beer::getFullName).toList());
        assertThat(beerNames).containsExactly((Object[]) beerNamesDb);

        // Compare actual and expected beer ids.
        JSONArray beerIDs = TestUtils.getValues(response.getBody(), "id");
        List<Long> longBeerIDs = this.beers.stream().map(Beer::getId).toList();
        List<Integer> intBeerIDs = TestUtils.convertLongListToIntList(longBeerIDs);
        Integer[] beerIDsDb = TestUtils.convertIdsToArray(intBeerIDs);
        assertThat(beerIDs).containsExactly((Object[]) beerIDsDb);

        int length = TestUtils.getLength(response.getBody());
        assertThat(length).isEqualTo(6);
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

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
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

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
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

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
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

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.CREATED, request, expected);
    }

    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (negative {@code volume} field).
     */
    @Test
    @DirtiesContext
    public void createBeerWithNegativeVolumeTest() {
        BeerRequestDTO request = new BeerRequestDTO();
        request.setBrand("Pilsner Urquell");
        request.setVolume(-1.0);

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST,
                request, null);
    }

    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (no {@code brand} field).
     */
    @Test
    @DirtiesContext
    public void createBeerWithNoBrandTest() {
        BeerRequestDTO request = new BeerRequestDTO();
        request.setVolume(0.6);
        request.setType("IPA");

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);
    }

    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (empty {@code brand} field).
     */
    @Test
    @DirtiesContext
    public void createBeerWithEmptyBrandTest() {
        BeerRequestDTO request = new BeerRequestDTO();
        request.setVolume(0.6);
        request.setType("IPA");
        request.setBrand("");

        TestUtils.assertCreatedBeerResponseIsCorrect(HttpStatus.BAD_REQUEST, request, null);

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
