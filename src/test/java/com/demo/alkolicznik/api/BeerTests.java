package com.demo.alkolicznik.api;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerDeleteDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerDeleteResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.JsonUtils.toModelList;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getBeersInCity;
import static com.demo.alkolicznik.utils.TestUtils.getImage;
import static com.demo.alkolicznik.utils.TestUtils.getRawPathToImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@ActiveProfiles("main")
@TestClassOrder(ClassOrderer.Random.class)
public class BeerTests {

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	@TestMethodOrder(MethodOrderer.Random.class)
	class GetRequests {

		private List<Beer> beers;

		@Autowired
		public GetRequests(List<Beer> beers) {
			this.beers = beers;
		}

		@Test
		@DisplayName("GET: '/api/beer/{beer_id}")
		public void getTest() {
			var getResponse = getRequest("/api/beer/1");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			BeerResponseDTO expected = createBeerResponse(1l, "Perla", "Chmielowa Pils", 0.5);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("GET: '/api/beer/{beer_id} [BEER_NOT_FOUND]")
		public void getNotExistingTest() {
			var getResponse = getRequest("/api/beer/9999");

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '9999' id",
					"/api/beer/9999");
		}

		@Test
		@DisplayName("GET: '/api/beer?city' sorted id asc")
		public void getAllInCityTest() {
			// given
			String city = "Olsztyn";

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

		@Test
		@DisplayName("GET: '/api/beer?city' [CITY_NOT_FOUND]")
		public void getAllInCityNotExistsTest() {
			var getResponse = getRequest("/api/beer", Map.of("city", "Jerzwald"));

			String jsonResponse = getResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"No such city: 'Jerzwald'",
					"/api/beer"
			);
		}

		@Test
		@DisplayName("GET: '/api/beer?city' city empty")
		public void getAllInCityEmptyTest() {
			var response = getRequestAuth("admin", "admin", "/api/beer", Map.of("city", "Gdansk"));

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
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	@TestMethodOrder(MethodOrderer.Random.class)
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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Lech", null, 0.5);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Karmi", null, 0.6);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Ksiazece", "Wisnia", 0.5);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Zywiec", "Jasne", 0.33);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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
					createBeerRequest("Perla", "Chmielowa Pils", 0.33)
			);
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Perla", "Chmielowa Pils", 0.33);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Heineken", null, 0.5);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

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

			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Heineken", null, 0.5);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// Fetch the newly-created beer.
			var getResponse = getRequest("/api/beer/8");

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(doubles = { 0d, -5d, -2.5 })
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
		@ValueSource(strings = { "", "\t \n " })
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
	@ActiveProfiles({ "main", "image" })
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	@TestMethodOrder(MethodOrderer.Random.class)
	class PutRequests {

		private List<Beer> beers;

		@Autowired
		public PutRequests(List<Beer> beers) {
			this.beers = beers;
		}

		@Test
		@DisplayName("PUT: '/api/beer' brand")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceWithBrandTest() {
			BeerRequestDTO request = createBeerRequest("Lech", null, null, null);

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
		@DisplayName("PUT: '/api/beer' brand & type")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceWithBrandAndTypeTest() {
			BeerRequestDTO request = createBeerRequest("Perla", "Miodowa", null, null);

			var putResponse = putRequestAuth("admin", "admin", "/api/beer/2", request);
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			BeerResponseDTO expected = createBeerResponse(2L, "Perla", "Miodowa", 0.5d);
			String expectedJson = toJsonString(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);
		}

		@Test
		@DisplayName("PUT: '/api/beer' brand, type & volume")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceWithBrandTypeAndVolumeTest() {
			BeerRequestDTO request = createBeerRequest("Zywiec", "Jasne", 0.33, null);

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
		@DisplayName("PUT: '/api/beer' brand, type, volume & image")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceWithBrandTypeVolumeAndImageTest() {
			BeerRequestDTO request = createBeerRequest("Zywiec", "Jasne",
					0.33, getRawPathToImage("zywiec-jasne-0.33.jpg"));

			var putResponse = putRequestAuth("admin", "admin", "/api/beer/6", request);
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			BeerResponseDTO expected = createBeerResponse(6L, "Zywiec", "Jasne", 0.33,
					createImageResponse("zywiec-jasne-0.33.jpg", actual.getImage()));
			String expectedJson = toJsonString(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);
		}

		@Test
		@DisplayName("PUT: '/api/beer' check image after replacing")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceBeerWithImageTest() {
			BeerRequestDTO request = createBeerRequest("Okocim", null, null, null);

			var putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			BeerResponseDTO expected = createBeerResponse(4L, "Okocim", null, 0.5d);
			String expectedJson = toJsonString(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/beer/4/image");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		}

		@Test
		@DisplayName("PUT: '/api/beer' check prices after replacing")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void replaceBeerWithPrices() {
			BeerRequestDTO request = createBeerRequest("Manufaktura Piwna", "Piwo na miodzie gryczanym", null, null);

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
				"7, Guinness, \t, null"
		},
				nullValues = { "null" })
		@DisplayName("PUT: '/api/beer' [BEERS_EQUAL]")
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
				"Guinness, \t, null"
		},
				nullValues = { "null" })
		@DisplayName("PUT: '/api/beer' [BEER_EXISTS]")
		public void replaceWithAlreadyExistingTest(String brand, String type, Double volume) {
			BeerRequestDTO request = createBeerRequest(brand, type, volume);

			var putResponse = putRequestAuth("admin", "admin", "/api/beer/2", request);

			assertIsError(putResponse.getBody(),
					HttpStatus.CONFLICT,
					"Beer already exists",
					"/api/beer/2");
		}

		@ParameterizedTest
		@ValueSource(longs = { -5, 0, 9999 })
		@DisplayName("PUT: '/api/beer' [BEER_NOT_FOUND]")
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
		@ValueSource(strings = { "", " \t" })
		@DisplayName("PUT: '/api/beer' [BRAND_BLANK]")
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
	@ActiveProfiles({ "main", "image" })
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	@TestMethodOrder(MethodOrderer.Random.class)
	public class PatchRequests {

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
			BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.5, null);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/3", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(
					3L, "Tyskie", "Gronie", 0.5, createImageResponse(getImage(3L, beers))
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
			BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null, null);
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
			BeerUpdateDTO request = createBeerUpdateRequest(null, "Potrojny zloty", null, null);
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
			BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.33);
			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/1", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(1L, "Perla", "Chmielowa Pils", 0.33);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// when
			var getResponse = getRequest("/api/beer/1/beer-price");
			List<BeerPriceResponseDTO> actualList = toModelList(getResponse.getBody(), BeerPriceResponseDTO.class);
			// then
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			List<BeerPriceResponseDTO> expectedPrices = getBeer(1L, beers).getPrices().stream().map(price -> {
				price.getBeer().setVolume(0.33);
				return new BeerPriceResponseDTO(price);
			}).toList();
			assertThat(actualList).hasSameElementsAs(expectedPrices);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' changing image does not remove prices")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void changingImageDoesNotRemovePricesTest() {
			// given
			String filename = "perla-chmielowa-pils-0.5.webp";
			BeerUpdateDTO request = createBeerUpdateRequest(null, null, null, getRawPathToImage(filename));
			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/1", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(
					1L, "Perla", "Chmielowa Pils", 0.5d,
					createImageResponse(filename, actual.getImage().getExternalId())
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// when
			var getResponse = getRequest("/api/beer/1/beer-price");
			List<BeerPriceResponseDTO> actualList = toModelList(getResponse.getBody(), BeerPriceResponseDTO.class);
			// then
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			List<BeerPriceResponseDTO> expectedPrices = getBeer(1L, beers).getPrices().stream().map(price -> {
				BeerPriceResponseDTO responseDTO = new BeerPriceResponseDTO(price);
				responseDTO.getBeer().setImage(
						createImageResponse(filename, actual.getImage().getExternalId())
				);
				return responseDTO;
			}).toList();
			assertThat(actualList).hasSameElementsAs(expectedPrices);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' remove type")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void updateRemoveTypeTest() {
			// given
			BeerUpdateDTO request = createBeerUpdateRequest(null, " ", null, null);
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
			BeerUpdateDTO request = createBeerUpdateRequest("Zubr", "Ciemnozloty", 0.5, null);

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
			BeerUpdateDTO request = createBeerUpdateRequest(null, null, null, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/6", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"No property to update was specified",
					"/api/beer/6"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [REMOVE_NULL_TYPE]")
		public void updateRemoveTypeWhenTypeIsNullTest() {
			// given
			BeerUpdateDTO request = createBeerUpdateRequest(null, " ", null);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/7", request);

			// then
			assertIsError(patchResponse.getBody(),
					HttpStatus.CONFLICT,
					"Objects are the same: nothing to update",
					"/api/beer/7");
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [VOLUME_NON_POSITIVE]")
		public void updateVolumeNonPositiveTest() {
			BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0d, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Volume must be a positive number",
					"/api/beer/4"
			);

			request = createBeerUpdateRequest(null, null, -5.1d, null);
			putResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);

			jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Volume must be a positive number",
					"/api/beer/4"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
		public void updateBeerNotFoundTest() {
			BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/321", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '321' id",
					"/api/beer/321"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [BRAND_BLANK]")
		public void updateBrandBlankTest() {
			BeerUpdateDTO request = createBeerUpdateRequest("\t \t \n\n\n", null, null, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Brand was not specified",
					"/api/beer/5"
			);

			request = createBeerUpdateRequest("", null, null, null);
			putResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);

			jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Brand was not specified",
					"/api/beer/5"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [PROPERTIES_SAME]")
		public void updateNothingToChangeTest() {
			BeerUpdateDTO request = createBeerUpdateRequest("Komes", "Porter Malinowy", 0.33, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/beer/5"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [PROPERTIES_SAME] (2)")
		public void updateNothingWasChangedTest() {
			BeerUpdateDTO request = createBeerUpdateRequest("Zubr", null, 0.5, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/4", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/beer/4"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/beer/{beer_id}' [BEER_EXISTS]")
		public void updateBeerAlreadyExistsTest() {
			BeerUpdateDTO request = createBeerUpdateRequest("Zubr", null, 0.5, null);
			var putResponse = patchRequestAuth("admin", "admin", "/api/beer/3", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.CONFLICT,
					"Beer already exists",
					"/api/beer/3"
			);
		}
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	@TestMethodOrder(MethodOrderer.Random.class)
	class DeleteRequests {

		private List<Beer> beers;

		@Autowired
		public DeleteRequests(List<Beer> beers) {
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
		@DisplayName("DELETE: '/api/beer'")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)

		public void deleteByObjectTest() {
			// given
			BeerRequestDTO request = createBeerRequest(getBeer(3L, beers));

			// when
			var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/3", request);
			assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = deleteResponse.getBody();
			BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);

			// then
			BeerDeleteDTO expected = createBeerDeleteResponse(
					getBeer(3L, beers),
					"Beer was deleted successfully!"
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// when
			var getResponse = getRequest("/api/beer/3");
			actualJson = getResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '3' id",
					"/api/beer/3");
		}

		@Test
		@DisplayName("DELETE: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
		public void deleteBeerNotFoundTest() {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/beer/0");

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '0' id",
					"/api/beer/0"
			);
		}

		// TODO: Move to 'SecuredEndpointTests'
		@Test
		@DisplayName("DELETE: '/api/beer' [INVALID_REQUEST; UNAUTHORIZED]")
		public void givenInvalidBody_whenUserIsUnauthorized_thenReturn404Test() {
			var deleteResponse = deleteRequestAuth("user", "user", "/api/beer",
					createBeerRequest(" ", "Porter Malinowy", -1d));

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Resource not found",
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

