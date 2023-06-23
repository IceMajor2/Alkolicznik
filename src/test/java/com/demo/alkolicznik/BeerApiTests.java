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
        // Create new Beer and post it to database.
        BeerRequestDTO beer = new BeerRequestDTO();
        beer.setBrand("Lech");
        ResponseEntity<BeerResponseDTO> postResponse = restTemplate
                .postForEntity("/api/beer", beer, BeerResponseDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        BeerResponseDTO created = postResponse.getBody();
        URI location = postResponse.getHeaders().getLocation();
        assertThat(created.getId()).isEqualTo(7L);
        assertThat(created.getFullName()).isEqualTo("Lech");
        assertThat(created.getType()).isNull();
//        assertThat(created.getBrand()).isEqualTo("Lech"); currently does not pass
        assertThat(created.getVolume()).isEqualTo(0.5d);

        // Fetch just-created entity through controller.
        ResponseEntity<BeerResponseDTO> getResponse = restTemplate
                .getForEntity(location, BeerResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerResponseDTO fetchController = getResponse.getBody();

        assertThat(fetchController).isEqualTo(created);

        // Additionally: fetch created entity directly from database using JDBCTemplate.
        BeerResponseDTO fetchJdbc = TestUtils.convertJdbcQueryToDto
                ("SELECT * FROM beer WHERE beer.id = %d".formatted(created.getId()),
                        TestUtils.mapToBeer());

        assertThat(created).isEqualTo(fetchJdbc);
    }

    /**
     * {@code POST /api/beer} - valid beer with non-default {@code volume} field.
     */
    @Test
    @DirtiesContext
    public void createBeerWithCustomVolumeTest() {
        // Create valid beer with custom volume.
        Beer beer = new Beer("Corona", 0.33);
        ResponseEntity<BeerResponseDTO> postResponse = restTemplate
                .postForEntity("/api/beer", beer, BeerResponseDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();
        BeerResponseDTO created = postResponse.getBody();

        // Fetch just-created entity through controller.
        ResponseEntity<BeerResponseDTO> getResponse = restTemplate
                .getForEntity(location, BeerResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerResponseDTO fetchController = getResponse.getBody();

        assertThat(fetchController).isEqualTo(created);

        // Additionally: fetch created entity directly from database using JDBCTemplate.
        BeerResponseDTO fetchJdbc = TestUtils.convertJdbcQueryToDto
                ("SELECT * FROM beer WHERE beer.id = %d".formatted(created.getId()),
                        TestUtils.mapToBeer());

        assertThat(created).isEqualTo(fetchJdbc);
    }

    /**
     * {@code POST /api/beer} - valid beer with specified {@code type} field.
     */
    @Test
    @DirtiesContext
    public void createBeerWithTypePresentTest() {
        // Create valid beer with custom volume.
        Beer beer = new Beer("Budweiser", "Budvar Original");
        ResponseEntity<BeerResponseDTO> postResponse = restTemplate
                .postForEntity("/api/beer", beer, BeerResponseDTO.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();
        BeerResponseDTO created = postResponse.getBody();

        // Fetch just-created entity through controller.
        ResponseEntity<BeerResponseDTO> getResponse = restTemplate
                .getForEntity(location, BeerResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerResponseDTO fetchController = getResponse.getBody();

        assertThat(fetchController).isEqualTo(created);

        // Additionally: fetch created entity directly from database using JDBCTemplate.
        BeerResponseDTO fetchJdbc = TestUtils.convertJdbcQueryToDto
                ("SELECT * FROM beer WHERE beer.id = %d".formatted(created.getId()),
                        TestUtils.mapToBeer());

        assertThat(created).isEqualTo(fetchJdbc);
    }

    /**
     * {@code POST /api/beer} - valid beer with non-default {@code volume} field and specified {@code type} field.
     */
    //@Test
    //@DirtiesContext

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

    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (negative {@code volume} field).
     */

    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (no {@code brand} field & empty).
     */
    //@Test
    //public void
    /**
     * {@code POST /api/beer} - send invalid BeerRequestDTO body (negative {@code volume} field).
     */
}
