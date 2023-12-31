package com.demo.alkolicznik.api;

import com.demo.alkolicznik.dto.beer.*;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.demo.alkolicznik.utils.matchers.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.FindingUtils.getBeer;
import static com.demo.alkolicznik.utils.FindingUtils.getBeersInCity;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "no-security", "no-vaadin"})
@TestClassOrder(ClassOrderer.Random.class)
public class BeerTest {

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class GetRequests {

        private List<Beer> beers;

        @Autowired
        public GetRequests(List<Beer> beers) {
            this.beers = beers;
        }

        @ParameterizedTest
        @CsvSource(value = {
                "1, Perla, Chmielowa Pils, 0.5",
                "3, Tyskie, Gronie, 0.65",
                "7, Guinness, null, 0.5"
        },
                nullValues = "null")
        @DisplayName("GET: '/api/beer/{beer_id}")
        public void getTest(Long id, String brand, String type, Double volume) {
            var getResponse = getRequest("/api/beer/" + id);
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(id, brand, type, volume, actual.getImage());
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(longs = {-5, 0, 9999})
        @DisplayName("GET: '/api/beer/{beer_id} [BEER_NOT_FOUND]")
        public void getNotExistingTest(Long id) {
            var getResponse = getRequest("/api/beer/" + id);

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + id + "' id",
                    "/api/beer/" + id);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Olsztyn", "Gdansk"})
        @DisplayName("GET: '/api/beer?city' sorted id asc")
        public void getAllInCityTest(String city) {
            // when
            var getResponse = getRequest("/api/beer", Map.of("city", city));
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();
            List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);

            // then
            List<BeerResponseDTO> expected = mapToDTO(getBeersInCity(city, beers));
            expected.sort(Comparator.comparing(BeerResponseDTO::getId));
            String expectedJson = toJsonString(expected);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Olsztynek", "Jerzwald", "Piecki"})
        @DisplayName("GET: '/api/beer?city' [CITY_NOT_FOUND]")
        public void getAllInCityNotExistsTest(String city) {
            var getResponse = getRequest("/api/beer", Map.of("city", city));

            String jsonResponse = getResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "No such city: '" + city + "'",
                    "/api/beer"
            );
        }

        @Test
        @DisplayName("GET: '/api/beer?city' city empty")
        public void getAllInCityEmptyTest() {
            var response = getRequestAuth("admin", "admin", "/api/beer",
                    Map.of("city", "Gdansk"));

            assertThat(response.getBody()).isEqualTo("[]");
        }

        @Test
        @DisplayName("GET: '/api/beer'")
        public void getAllTest() {
            // when
            var getResponse = getRequestAuth("admin", "admin", "/api/beer");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = getResponse.getBody();
            List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);

            // then
            List<BeerResponseDTO> expected = mapToDTO(beers);
            expected.sort(Comparator.comparing(BeerResponseDTO::getId));
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class PostRequests {

        private List<Beer> beers;

        @Autowired
        public PostRequests(List<Beer> beers) {
            this.beers = beers;
        }

        @Test
        @DisplayName("POST: '/api/beer'")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addBrandTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Lech", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + (beers.size() + 1));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and volume only")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addBrandAndVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Karmi", null, 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + (beers.size() + 1));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and type only")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addBrandAndTypeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Ksiazece", "Wisnia", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + (beers.size() + 1));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' everything specified")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addEverythingSpecifiedTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Zywiec", "Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + (beers.size() + 1));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' exists by fullname, volume unique")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addFullnameExistsVolumeUniqueTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", 0.6)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Perla", "Chmielowa Pils", 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + (beers.size() + 1));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' type blank")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addWithTypeBlankTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Heineken", " \t\t \t", null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(((beers.size() + 1)), "Heineken", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + ((beers.size() + 1)));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' empty type")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void addWithTypeEmptyTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Heineken", "", null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse((beers.size() + 1), "Heineken", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/" + ((beers.size() + 1)));

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0d, -5d, -2.5})
        @DisplayName("POST: '/api/beer' [VOLUME_NON_POSITIVE]")
        public void addVolumeNegativeTest(Double volume) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Pilsner Urquell", null, volume)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "\t \n "})
        @DisplayName("POST: '/api/beer' [BRAND_BLANK]")
        public void addBrandNullTest(String brand) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(brand, "Jasne Okocimskie", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "Perla, Chmielowa Pils, null",
                "Zubr, null, null",
                "Tyskie, Gronie, 0.65"
        },
                nullValues = "null")
        @DisplayName("POST: '/api/beer' [BEER_EXISTS]")
        public void addBeerExistsTest(String brand, String type, Double volume) {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(brand, type, volume)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_BLANK; VOLUME_NEGATIVE]")
        public void addBrandBlankVolumeNegativeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("\t", null, -15.9)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified; Volume must be a positive number",
                    "/api/beer");
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class PutRequests {

        private List<Beer> beers;

        @Autowired
        public PutRequests(List<Beer> beers) {
            this.beers = beers;
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' brand")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void replaceWithBrandTest() {
            BeerRequestDTO request = createBeerRequest("Lech", null, null);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/1", request);
            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = putResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(1L, "Lech", null, 0.5d);
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' brand & type")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void replaceWithBrandAndTypeTest() {
            BeerRequestDTO request = createBeerRequest("Perla", "Biala", null);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/2", request);
            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = putResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(2L, "Perla", "Biala", 0.5d);
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' brand, type & volume")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void replaceWithBrandTypeAndVolumeTest() {
            BeerRequestDTO request = createBeerRequest("Zywiec", "Jasne", 0.33);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/2", request);
            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = putResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(2L, "Zywiec", "Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' brand, type, volume & image")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void replaceWithBrandTypeVolumeAndImageTest() {
            BeerRequestDTO request = createBeerRequest("Zywiec", "Jasne", 0.33);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/6", request);
            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = putResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(6L, "Zywiec", "Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' check prices after replacing")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void replaceBeerWithPrices() {
            BeerRequestDTO request = createBeerRequest("Manufaktura Piwna", "Piwo na miodzie gryczanym", null);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);
            assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = putResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(5L, "Manufaktura Piwna", "Piwo na miodzie gryczanym", 0.5d);
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/5/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(getResponse.getBody()).isEqualTo("[]");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "2, Ksiazece, Zlote pszeniczne, null",
                "4, Zubr, null, null",
                "5, Komes, Porter Malinowy, 0.33",
                "7, Guinness, '\n  \t', null"
        },
                nullValues = {"null"})
        @DisplayName("PUT: '/api/beer/{beer_id}' [BEERS_EQUAL]")
        public void replaceWithSameValuesTest(Long id, String brand, String type, Double volume) {
            BeerRequestDTO request = createBeerRequest(brand, type, volume);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/" + id, request);

            assertIsError(putResponse.getBody(),
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/beer/" + id);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "Perla, Chmielowa Pils, null",
                "Tyskie, Gronie, 0.65",
                "Zubr, null, null",
                "Guinness, '\n  \t', null"
        },
                nullValues = {"null"})
        @DisplayName("PUT: '/api/beer/{beer_id}' [BEER_EXISTS]")
        public void replaceWithAlreadyExistingTest(String brand, String type, Double volume) {
            BeerRequestDTO request = createBeerRequest(brand, type, volume);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/2", request);

            assertIsError(putResponse.getBody(),
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer/2");
        }

        @ParameterizedTest
        @ValueSource(longs = {-5, 0, 9999})
        @DisplayName("PUT: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
        public void replaceNonExistingTest(Long id) {
            BeerRequestDTO request = createBeerRequest("Ksiazece", "Wisniowe", 0.6);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/" + id, request);

            assertIsError(putResponse.getBody(),
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + id + "' id",
                    "/api/beer/" + id);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " \t"})
        @DisplayName("PUT: '/api/beer/{beer_id}' [BRAND_BLANK]")
        public void replaceBrandBlankTest(String brand) {
            BeerRequestDTO brandBlank = createBeerRequest(brand, "Cerny", null);

            var putResponse = putRequestAuth("admin", "admin", "/api/beer/1", brandBlank);

            assertIsError(putResponse.getBody(),
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer/1");
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class PatchRequests {

        private List<Beer> beers;

        @Autowired
        public PatchRequests(List<Beer> beers) {
            this.beers = beers;
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' volume update")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void updateVolumeTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.5);

            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/3", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(
                    3L, "Tyskie", "Gronie", 0.5
            );
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            // when
            var getResponse = getRequest("/api/beer/3");

            // then
            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' brand update")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void updateBrandTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/6", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(
                    6L, "Ksiazece", "Biale", 0.5
            );
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            // when
            var getResponse = getRequest("/api/beer/6");

            // then
            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' type update")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void updateTypeTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Potrojny zloty", null);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(
                    5L, "Komes", "Potrojny zloty", 0.33
            );
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            // when
            var getResponse = getRequest("/api/beer/5");

            // then
            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' changing brand removes prices")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void changingBrandRemovesPricesTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest("Harnas", null, null);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(4L, "Harnas", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getResponse = getRequest("/api/beer/4/beer-price");
            // then
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(getResponse.getBody()).isEqualTo("[]");
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' changing type removes prices")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void changingTypeRemovesPricesTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Ciemnozloty", null);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(4L, "Zubr", "Ciemnozloty", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getResponse = getRequest("/api/beer/4/beer-price");
            // then
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(getResponse.getBody()).isEqualTo("[]");
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' changing volume does not remove prices")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void changingVolumeDoesNotRemovePricesTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.6);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/1", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(1L, "Perla", "Chmielowa Pils", 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getResponse = getRequest("/api/beer/1/beer-price");
            List<BeerPriceResponseDTO> actualList = toModelList(getResponse.getBody(), BeerPriceResponseDTO.class);
            // then
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<BeerPriceResponseDTO> expectedPrices = getBeer(1L, beers).getPrices().stream().map(price -> {
                price.getBeer().setVolume(0.6);
                return new BeerPriceResponseDTO(price);
            }).toList();
            assertThat(actualList).hasSameElementsAs(expectedPrices);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' remove type")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void updateRemoveTypeTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, " ", null);
            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(
                    5L, "Komes", null, 0.33
            );
            String expectedJson = toJsonString(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);

            // when
            var getResponse = getRequest("/api/beer/5");

            // then
            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' add type")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void updateAddTypeTest() {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest("Zubr", "Ciemnozloty", 0.5);

            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = patchResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            BeerResponseDTO expected = createBeerResponse(4L, "Zubr", "Ciemnozloty", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getResponse = getRequest("/api/beer/4");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            // then
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PATCH: '/api/beer/{beer_id}' [NO_PROPERTY_SPECIFIED]")
        public void updateWithEmptyBodyTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, null);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/6", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "No property to update was specified",
                    "/api/beer/6"
            );
        }

        @ParameterizedTest
        @CsvSource(value = {
                "4, ''",
                "7, '\n  \t'"
        })
        @DisplayName("PATCH: '/api/beer/{beer_id}' [REMOVE_TYPE_ALREADY_NULL]")
        public void updateRemoveTypeWhenTypeIsNullTest(Long toUpdateId, String type) {
            // given
            BeerUpdateDTO request = createBeerUpdateRequest(null, type, null);

            // when
            var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + toUpdateId, request);

            // then
            assertIsError(patchResponse.getBody(),
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/beer/" + toUpdateId);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-0.9, 0d, -15d})
        @DisplayName("PATCH: '/api/beer/{beer_id}' [VOLUME_NON_POSITIVE]")
        public void updateVolumeNonPositiveTest(Double volume) {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, volume);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer/4"
            );
        }

        @ParameterizedTest
        @ValueSource(longs = {0, -23, 9998})
        @DisplayName("PATCH: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
        public void updateBeerNotFoundTest(Long id) {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/" + id, request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + id + "' id",
                    "/api/beer/" + id
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "\t", "\n"})
        @DisplayName("PATCH: '/api/beer/{beer_id}' [BRAND_BLANK]")
        public void updateBrandBlankTest(String brand) {
            BeerUpdateDTO request = createBeerUpdateRequest(brand, null, null);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer/5"
            );
        }

        @ParameterizedTest
        @CsvSource(value = {
                "1, Perla, Chmielowa Pils, null",
                "4, Zubr, , null",
                "6, Miloslaw, Biale, 0.5",
                "7, Guinness, ' ', 0.5"
        },
                nullValues = "null")
        @DisplayName("PATCH: '/api/beer/{beer_id}' [PROPERTIES_SAME] (2)")
        public void updateNothingWasChangedTest(Long toUpdateId, String brand, String type, Double volume) {
            BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/" + toUpdateId, request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/beer/" + toUpdateId
            );
        }

        @ParameterizedTest
        @CsvSource(value = {
                "1, Zubr, '', null",
                "2, Guinness, ' \t', null",
                "5, Tyskie, Gronie, 0.65",
                "8, null, Chmielowa Pils, null",
                "9, null, null, 0.5"
        },
                nullValues = "null")
        @DisplayName("PATCH: '/api/beer/{beer_id}' [BEER_EXISTS]")
        public void updateBeerAlreadyExistsTest(Long toUpdateId, String brand, String type, Double volume) {
            BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);
            var putResponse = patchRequestAuth("admin", "admin", "/api/beer/" + toUpdateId, request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer/" + toUpdateId
            );
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class DeleteRequestsPath {

        private List<Beer> beers;

        @Autowired
        public DeleteRequestsPath(List<Beer> beers) {
            this.beers = beers;
        }

        @Test
        @DisplayName("DELETE: '/api/beer/{beer_id}'")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void deleteByIdTest() {
            // given
            Long beerId = 6L;

            // when
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/6");
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = deleteResponse.getBody();
            BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);

            // then
            BeerDeleteDTO expected = createBeerDeleteResponse(
                    getBeer(6L, beers),
                    "Beer was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getRequest = getRequest("/api/beer/6");
            actualJson = getRequest.getBody();

            // then
            assertIsError(actualJson,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '6' id",
                    "/api/beer/6");
        }

        @Test
        @DisplayName("DELETE: '/api/beer/{beer_id}' also removes prices")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void deleteBeerAlsoRemovesPricesTest() {
            // given
            Long beerId = 5L;

            // when
            var getResponse = getRequestAuth("admin", "admin", "/api/beer/" + beerId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            // when
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId);
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = deleteResponse.getBody();
            BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);

            // then
            BeerDeleteDTO expected = createBeerDeleteResponse(
                    getBeer(beerId, beers),
                    "Beer was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            getResponse = getRequestAuth("admin", "admin", "/api/beer/" + beerId + "/beer-price");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @ParameterizedTest
        @ValueSource(longs = {-2, 0, 5555})
        @DisplayName("DELETE: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
        public void deleteBeerNotFoundTest(Long id) {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer/" + id);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + id + "' id",
                    "/api/beer/" + id
            );
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.Random.class)
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    class DeleteRequestsObject {

        private List<Beer> beers;

        @Autowired
        public DeleteRequestsObject(List<Beer> beers) {
            this.beers = beers;
        }

        @ParameterizedTest
        @CsvSource(value = {
                "3, Tyskie, Gronie, 0.65",
                "4, Zubr, ' ', null",
                "1, Perla, Chmielowa Pils, null",
                "7, Guinness, null, null"
        }, nullValues = "null")
        @DisplayName("DELETE: '/api/beer'")
        @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
        public void deleteByObjectTest(Long id, String brand, String type, Double volume) {
            // given
            BeerRequestDTO request = createBeerRequest(brand, type, volume);

            // when
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = deleteResponse.getBody();
            BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);

            // then
            BeerDeleteDTO expected = createBeerDeleteResponse(
                    getBeer(id, beers),
                    "Beer was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // when
            var getResponse = getRequest("/api/beer/" + id);
            actualJson = getResponse.getBody();

            // then
            assertIsError(actualJson,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '" + id + "' id",
                    "/api/beer/" + id);
        }

        @ParameterizedTest
        @CsvSource(value = {
                "Perla, Biala, 0.5",
                "Lech, null, null",
                "Tyskie, Gronie, null",
                "Zubr, null, 0.33",
                "Komes, Porter Malinowy, null"
        },
                nullValues = "null")
        @DisplayName("DELETE: '/api/beer' [BEER_NOT_FOUND]")
        public void deleteBeerByObjectNotFoundTest(String brand, String type, Double volume) {
            BeerRequestDTO request = createBeerRequest(brand, type, volume);
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer: [%s, %.2fl]"
                            .formatted(request.getFullName(), request.getVolume()),
                    "/api/beer"
            );
        }

        @ParameterizedTest
        @ValueSource(doubles = {-53.2, -0.5, 0d})
        @DisplayName("DELETE: '/api/beer' [VOLUME_NON_POSITIVE]")
        public void deleteBeerByObjectVolumeNonPositiveTest(Double volume) {
            BeerRequestDTO request = createBeerRequest("Komes", "Porter Malinowy", volume);
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer"
            );
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"   ", "", "\t"})
        @DisplayName("DELETE: '/api/beer' [BRAND_BLANK]")
        public void deleteBeerByObjectBrandBlankTest(String brand) {
            BeerRequestDTO request = createBeerRequest(brand, null, 0.5);
            var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer"
            );
        }
    }

    private List<BeerResponseDTO> mapToDTO(List<Beer> beers) {
        return beers.stream()
                .map(BeerResponseDTO::new)
                .collect(Collectors.toList());
    }
}
