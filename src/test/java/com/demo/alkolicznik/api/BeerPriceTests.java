package com.demo.alkolicznik.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beerprice.BeerPriceDeleteDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.BeerPrice;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerPriceDeleteResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerPriceRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerPriceResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerPriceUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.JsonUtils.toModelList;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getImage;
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.MockRequests.mockDeleteRequest;
import static com.demo.alkolicznik.utils.requests.MockRequests.mockGetRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.putRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@AutoConfigureMockMvc
@ActiveProfiles("main")
public class BeerPriceTests {

	@Autowired
	private List<Store> stores;

	@Autowired
	private List<Beer> beers;

	public static MockMvc mockMvc;

	@Autowired
	public void setMockMvc(MockMvc mockMvc) {
		BeerPriceTests.mockMvc = mockMvc;
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class GetRequests {

		@ParameterizedTest
		@CsvSource({
				"2, 4, 2.89",
				"4, 1, 4.09",
				"5, 6, 6.09"
		})
		@DisplayName("GET: '/api/beer-price'")
		public void getBeerPriceTest(Long storeId, Long beerId, Double price) {
			var getResponse = getRequest("/api/beer-price",
					Map.of("store_id", storeId, "beer_id", beerId));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(beerId, beers)),
					createStoreResponse(getStore(storeId, stores)),
					"PLN " + price
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(longs = { 0, -5, 912 })
		@DisplayName("GET: '/api/beer-price' [BEER_NOT_FOUND]")
		public void getBeerPriceBeerNotExistsTest(Long beerId) {
			var getResponse = getRequest("/api/beer-price",
					Map.of("store_id", 5L, "beer_id", beerId));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '" + beerId + "' id",
					"/api/beer-price");
		}

		@ParameterizedTest
		@ValueSource(longs = { -2111, 0, 91234 })
		@DisplayName("GET: '/api/beer/beer-price' [STORE_NOT_FOUND]")
		public void getBeerPriceStoreNotExistsTest(Long storeId) {
			var getResponse = getRequest("/api/beer-price",
					Map.of("store_id", storeId, "beer_id", 3L));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + storeId + "' id",
					"/api/beer-price");
		}

		@ParameterizedTest
		@CsvSource({
				"1, 4",
				"3, 2",
				"7, 1",
				"6, 7",
				"9, 9"
		})
		@DisplayName("GET: '/api/beer-price' [NO_PRICE]")
		public void getBeerPriceNotExistsTest(Long storeId, Long beerId) {
			var getResponse = getRequest("/api/beer-price",
					Map.of("store_id", storeId, "beer_id", beerId));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Store does not currently sell this beer",
					"/api/beer-price");
		}

		@ParameterizedTest
		@CsvSource({
				"0, 111",
				"5423, 919",
				"-318, 546238"
		})
		@DisplayName("GET: '/api/beer-price' [STORE_n_BEER_NOT_FOUND]")
		public void getBeerPriceBeerAndStoreNotExistsTest(Long storeId, Long beerId) {
			var getResponse = getRequest("/api/beer-price",
					Map.of("store_id", storeId, "beer_id", beerId));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '" + beerId + "' id; Unable to find store of '" + storeId + "' id",
					"/api/beer-price");
		}

		@ParameterizedTest
		@ValueSource(strings = { "Olsztyn", "Warszawa" })
		@DisplayName("GET: '/api/beer-price?city' ordered")
		public void getBeerPricesFromCityTest(String city) {
			var getResponse = getRequest("/api/beer-price", Map.of("city", city));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

			List<Store> cityStores = stores.stream()
					.filter(store -> store.getCity().equals(city))
					.collect(Collectors.toList());

			List<BeerPriceResponseDTO> expected = new ArrayList<>();
			for (Store store : cityStores) {
				for (BeerPrice beer : store.getPrices()) {
					expected.add(new BeerPriceResponseDTO(beer));
				}
			}
			sortByBeerIdPriceAndStoreId(expected);
			assertThat(actual).containsExactlyElementsOf(expected);
		}

		@ParameterizedTest
		@ValueSource(strings = { "Ilawa", "Gdansk" })
		@DisplayName("GET: '/api/beer-price?city' of empty city")
		public void getBeerPricesFromCityEmptyTest(String city) {
			// when
			var getResponse = getRequest("/api/beer-price", Map.of("city", city));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = getResponse.getBody();

			// then
			String expectedJson = "[]";
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(strings = { "abdf", "skjo", "" })
		@DisplayName("GET: '/api/beer-price?city' [CITY_NOT_FOUND]")
		public void getBeerPricesFromCityNotExistsTest(String city) {
			var getResponse = getRequest("/api/beer-price", Map.of("city", city));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"No such city: '" + city + "'",
					"/api/beer-price");
		}

		@ParameterizedTest
		@ValueSource(longs = { 5, 1, 6 })
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price' ordered")
		public void getBeerPricesOfBeerTest(Long beerId) {
			var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

			Beer beer = getBeer(beerId.longValue(), beers);
			List<BeerPriceResponseDTO> expected = beer.getPrices()
					.stream()
					.map(BeerPriceResponseDTO::new)
					.collect(Collectors.toList());
			sortByCityPriceAndStoreId(expected);
			assertThat(actual).containsExactlyElementsOf(expected);
		}

		@ParameterizedTest
		@ValueSource(longs = { -532, 0, 5328 })
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price' [BEER_NOT_FOUND]")
		public void getBeerPricesOfBeerNotExistsTest(Long beerId) {
			var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '" + beerId + "' id",
					"/api/beer/" + beerId + "/beer-price");
		}

		@ParameterizedTest
		@ValueSource(longs = { 7, 8, 9 })
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price' beer not sold")
		public void getBeerPricesOfBeerNotSoldTest(Long beerId) {
			var getResponse = getRequest("/api/beer/" + beerId + "/beer-price");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String jsonResponse = getResponse.getBody();
			assertThat(jsonResponse).isEqualTo("[]");
		}

		@ParameterizedTest
		@CsvSource({
				"4, Warszawa",
				"1, Olsztyn",
				"2, Olsztyn"
		})
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price?city' ordered")
		public void getBeersPriceInCityTest(Long beerId, String city) {
			// when
			var getResponse = getRequest("/api/beer/" + beerId + "/beer-price",
					Map.of("city", city));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = getResponse.getBody();
			List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

			// then
			Beer expectedBeer = getBeer(beerId.longValue(), beers);
			List<BeerPriceResponseDTO> expected = new ArrayList<>();
			for (BeerPrice beerPrice : expectedBeer.getPrices()) {
				if (beerPrice.getStore().getCity().equals(city)) {
					expected.add(new BeerPriceResponseDTO(beerPrice));
				}
			}
			sortByPriceAndStoreId(expected);
			assertThat(actual).containsExactlyElementsOf(expected);
		}

		@Test
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price?city' empty city")
		public void getBeersPriceInCityCityEmptyTest() {
			var getResponse = getRequest("/api/beer/4/beer-price", Map.of("city", "Gdansk"));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();

			String expectedJson = "[]";

			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price' [BEER_NOT_FOUND]")
		public void getBeersPriceInCityBeerNotExistsTest() {
			var getResponse = getRequest("/api/beer/10/beer-price", Map.of("city", "Gdansk"));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '10' id",
					"/api/beer/10/beer-price");
		}

		@Test
		@DisplayName("GET: '/api/beer/{beer_id}/beer-price' [CITY_NOT_FOUND]")
		public void getBeersPriceInCityCityNotExistsTest() {
			var getResponse = getRequest("/api/beer/5/beer-price", Map.of("city", "Ciechocinek"));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"No such city: 'Ciechocinek'",
					"/api/beer/5/beer-price");
		}

		@Test
		@DisplayName("GET: '/api/store/{store_id}/beer-price'")
		public void getBeerPricesFromStoreTest() {
			var getResponse = getRequest("/api/store/3/beer-price");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

			Store store = getStore(3L, stores);
			List<BeerPriceResponseDTO> expected = store.getPrices().stream()
					.map(BeerPriceResponseDTO::new)
					.toList();
			assertThat(actual).containsExactlyElementsOf(expected);
		}

		@Test
		@DisplayName("GET: '/api/store/{store_id}/beer-price' [STORE_NOT_FOUND]")
		public void getBeerPricesFromStoreNotExistsTest() {
			var getResponse = getRequest("/api/store/8/beer-price");

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '8' id",
					"/api/store/8/beer-price");
		}

		@Test
		@DisplayName("GET: '/api/store/{store_id}/beer-price' empty store")
		public void getBeerPricesFromStoreEmptyTest() {
			var getResponse = getRequest("/api/store/7/beer-price");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();

			String expectedJson = "[]";
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("GET: '/api/beer-price'")
		public void getBeerPricesAllArrayTest() {
			List<BeerPriceResponseDTO> expected = new ArrayList<>();
			for (Store store : stores) {
				for (BeerPrice beerPrice : store.getPrices()) {
					expected.add(new BeerPriceResponseDTO(beerPrice));
				}
			}
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(mockGetRequest("/api/beer-price"),
					HttpStatus.OK, expectedJson);
			List<BeerPriceResponseDTO> actual = toModelList(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PostRequestsParam {

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (params)")
		@DirtiesContext
		public void addBeerPriceIdTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/1/beer-price",
					Map.of("beer_id", 3L, "beer_price", 4.19));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(3L, beers)),
					createStoreResponse(getStore(1L, stores)),
					"PLN 4.19"
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getResponse = getRequest("/api/beer-price",
					Map.of("beer_id", 3L, "store_id", 1L));

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (params) [STORE_NOT_EXISTS]")
		public void addBeerPriceIdStoreNotExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/9999/beer-price",
					Map.of("beer_id", 3L, "beer_price", 4.19));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '9999' id",
					"/api/store/9999/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (params) [VOLUME_NON_POSITIVE]")
		public void addBeerPriceIdVolumeNegativeAndZeroTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					Map.of("beer_id", 5L, "beer_price", 0d));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/store/2/beer-price");

			postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					Map.of("beer_id", 5L, "beer_price", -5.213));

			jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/store/2/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (params) [BEER_NOT_FOUND]")
		public void addBeerPriceIdBeerNotExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/1/beer-price",
					Map.of("beer_id", 999L, "beer_price", 6.69));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '999' id",
					"/api/store/1/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (params) [BEER_PRICE_EXISTS]")
		public void addBeerPriceIdAlreadyExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/1/beer-price",
					Map.of("beer_id", 2, "beer_price", 6.69));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.CONFLICT,
					"Beer is already in store",
					"/api/store/1/beer-price");
		}
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PostRequestsObject {

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto)")
		@DirtiesContext
		public void addBeerPriceToStoreTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					createBeerPriceRequest("Perla Chmielowa Pils", 0.5, 3.69));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(1L, beers)),
					createStoreResponse(getStore(2L, stores)),
					"PLN 3.69"
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getResponse = getRequest("/api/beer-price",
					Map.of("beer_id", 1L, "store_id", 2L));

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) (2)")
		@DirtiesContext
		public void addBeerPriceToStoreTest2() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/1/beer-price",
					createBeerPriceRequest("Zubr", 0.5, 2.79));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(4L, beers),
							createImageResponse(getImage(4L, beers))),
					createStoreResponse(getStore(1L, stores)),
					"PLN 2.79"
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getResponse = getRequest("/api/beer-price",
					Map.of("beer_id", 4L, "store_id", 1L));

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) with no volume")
		@DirtiesContext
		public void addBeerPriceToStoreDefaultVolumeTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/7/beer-price",
					createBeerPriceRequest("Perla Chmielowa Pils", null, 3.69));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(1L, beers)),
					createStoreResponse(getStore(7L, stores)),
					"PLN 3.69");
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getResponse = getRequest("/api/beer-price",
					Map.of("beer_id", 1L, "store_id", 7L));

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BEER_PRICE_EXISTS]")
		public void addBeerPriceAlreadyExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/1/beer-price",
					createBeerPriceRequest("Komes Porter Malinowy", 0.33, 8.09));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.CONFLICT,
					"Beer is already in store",
					"/api/store/1/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BEER_OF_DIFFERENT_VOL]")
		public void createBeerPriceBeerExistsButDifferentVolumeTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					createBeerPriceRequest("Zubr", 0.6, 3.19));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer 'Zubr' of '%.2f' volume".formatted(0.6),
					"/api/store/2/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [VOLUME_NON_POSITIIVE]")
		public void createBeerPriceNegativeAndZeroVolumeTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/6/beer-price",
					createBeerPriceRequest("Tyskie Gronie", -1.0, 3.09));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Volume must be a positive number",
					"/api/store/6/beer-price");

			postResponse = postRequestAuth("admin", "admin",
					"/api/store/6/beer-price",
					createBeerPriceRequest("Tyskie Gronie", 0d, 3.09));

			jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Volume must be a positive number",
					"/api/store/6/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BRAND_NULL]")
		public void createBeerPriceBrandNullTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/5/beer-price",
					createBeerPriceRequest(null, 0.5, 3.09));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Beer was not specified",
					"/api/store/5/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BRAND_BLANK]")
		public void createBeerPriceBrandBlankAndEmptyTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/5/beer-price",
					createBeerPriceRequest("", 0.5, 3.09));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Beer was not specified",
					"/api/store/5/beer-price");

			postResponse = postRequestAuth("admin", "admin",
					"/api/store/3/beer-price",
					createBeerPriceRequest(" \t \n\n \t", 1d, 7.99));

			jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Beer was not specified",
					"/api/store/3/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [PRICE_NULL]")
		public void createBeerPriceNegativeAndZeroPriceTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					createBeerPriceRequest("Kormoran Miodne", 0.5, -1d));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/store/2/beer-price");

			postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					createBeerPriceRequest("Kormoran Miodne", 0.5, 0d));

			jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/store/2/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BEER_NOT_SPECIFIED; PRICE_ZERO, VOLUME_NEGATIVE]")
		public void createBeerPricePriceNullTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/2/beer-price",
					createBeerPriceRequest(null, 0d, -9.4));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Beer was not specified; Price must be a positive number; Volume must be a positive number",
					"/api/store/2/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [BEER_NOT_FOUND]")
		public void createBeerPriceBeerNotExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/4/beer-price",
					createBeerPriceRequest("Kormoran Miodne", 0.5, 7.99));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of 'Kormoran Miodne' name",
					"/api/store/4/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' (dto) [STORE_NOT_FOUND]")
		@DirtiesContext
		public void createBeerPriceStoresNotExistsTest() {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store/9999/beer-price",
					createBeerPriceRequest("Ksiazece Zlote pszeniczne", 0.5, 3.79));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '9999' id",
					"/api/store/9999/beer-price");
		}

		@Test
		@DisplayName("POST: '/api/store/{store_id}/beer-price' [INVALID_REQUEST; UNAUTHORIZED]")
		public void givenInvalidBody_whenUserIsUnauthorized_thenReturn404Test() {
			var postResponse = postRequestAuth("user", "user", "/api/store/3/beer-price",
					createBeerPriceRequest("\t", 0d, -5d));

			String jsonResponse = postResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Resource not found",
					"/api/store/3/beer-price"
			);
		}
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PutRequests {

		@Test
		@DisplayName("PUT: '/api/beer-price'")
		@DirtiesContext
		public void updateBeerPricePriceTest() {
			// given
			BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(4.59);

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/beer-price",
					request, Map.of("beer_id", 3L, "store_id", 3L));
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();

			BeerPriceResponseDTO actual = toModel(actualJson, BeerPriceResponseDTO.class);

			// then
			BeerPriceResponseDTO expected = createBeerPriceResponse(
					createBeerResponse(getBeer(3L, beers),
							createImageResponse(getImage(3L, beers))),
					createStoreResponse(getStore(3L, stores)),
					"PLN 4.59"
			);

			String expectedJson = toJsonString(expected);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getResponse = getRequest("/api/beer-price", Map.of("beer_id", 3L, "store_id", 3L));

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, BeerPriceResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/beer-price' [PRICE_NON_POSITIVE]")
		public void updateBeerPricePriceNegativeAndZeroTest() {
			BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(0d);
			var putResponse = putRequestAuth("admin", "admin",
					"/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

			String jsonResponse = putResponse.getBody();
			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/beer-price");

			request = createBeerPriceUpdateRequest(-5.9);
			putResponse = putRequestAuth("admin", "admin",
					"/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

			jsonResponse = putResponse.getBody();
			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Price must be a positive number",
					"/api/beer-price");
		}

		@Test
		@DisplayName("PUT: '/api/beer-price' [PROPERTIES_NOT_SPECIFIED]")
		public void updateBeerPricePriceNullTest() {
			BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(null);
			var putResponse = putRequestAuth("admin", "admin",
					"/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 3L));

			String jsonResponse = putResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"No property to update was specified",
					"/api/beer-price");
		}

		@Test
		@DisplayName("PUT: '/api/beer-price' [PROPERTIES_SAME]")
		public void updateBeerPricePropertiesSameTest() {
			BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(2.89);
			var putResponse = putRequestAuth("admin", "admin",
					"/api/beer-price", request, Map.of("beer_id", 4L, "store_id", 2L));

			String jsonResponse = putResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/beer-price");
		}

		@Test
		@DisplayName("PUT: '/api/beer-price' [INVALID_REQUEST; UNAUTHORIZED]")
		public void givenInvalidBody_whenUserIsUnauthorized_thenReturn404Test() {
			var putResponse = putRequest("/api/beer-price",
					createBeerPriceUpdateRequest(-5d),
					Map.of("store_id", 2, "beer_id", 2));

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Resource not found",
					"/api/beer-price"
			);
		}
	}

	@Nested
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class DeleteRequests {

		@Test
		@DisplayName("DELETE: '/api/beer-price'")
		@DirtiesContext
		@WithUserDetails("admin")
		public void deleteBeerPriceTest() {
			BeerPriceDeleteDTO expected = createBeerPriceDeleteResponse(
					getBeer(2L, beers),
					getStore(5L, stores),
					"5.49 PLN",
					"Beer price was deleted successfully!"
			);
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(mockDeleteRequest("/api/beer-price",
							Map.of("beer_id", 2L, "store_id", 5L)),
					HttpStatus.OK,
					expectedJson);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getRequest = getRequest("/api/beer-price", Map.of("beer_id", 2L, "store_id", 5L));

			String jsonResponse = getRequest.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Store does not currently sell this beer",
					"/api/beer-price");
		}

		@Test
		@DisplayName("DELETE: '/api/beer-price' [STORE_NOT_FOUND]")
		public void deleteBeerPriceStoreNotExistsTest() {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/beer-price", Map.of("store_id", 913L, "beer_id", 3L));

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '913' id",
					"/api/beer-price"
			);
		}

		@Test
		@DisplayName("DELETE: '/api/beer-price' [BEER_NOT_FOUND]")
		public void deleteBeerPriceBeerNotExistsTest() {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/beer-price", Map.of("store_id", 3L, "beer_id", 433L));

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find beer of '433' id",
					"/api/beer-price"
			);
		}

		@Test
		@DisplayName("DELETE: '/api/beer-price' [BEER_PRICE_NOT_FOUND]")
		public void deleteBeerPricePriceNotExistsTest() {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/beer-price", Map.of("store_id", 5L, "beer_id", 1L));

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Store does not currently sell this beer",
					"/api/beer-price"
			);
		}
	}

	private void sortByBeerIdPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
		Comparator<Object> comparator = Comparator
				.comparing(p -> ((BeerPriceResponseDTO) p).getBeer().getId())
				.thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
				.thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
		Collections.sort(pricesDTO, comparator);
	}

	private void sortByCityPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
		Comparator<Object> comparator = Comparator
				.comparing(p -> ((BeerPriceResponseDTO) p).getStore().getCity())
				.thenComparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
				.thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
		Collections.sort(pricesDTO, comparator);
	}

	private void sortByPriceAndStoreId(List<BeerPriceResponseDTO> pricesDTO) {
		Comparator<Object> comparator = Comparator
				.comparing(p -> ((BeerPriceResponseDTO) p).getAmountOnly())
				.thenComparing(p -> ((BeerPriceResponseDTO) p).getStore().getId());
		Collections.sort(pricesDTO, comparator);
	}
//	@ParameterizedTest
//	@CsvSource(value = {
//			"4, Lubi, Olsztyn, ul. Michala Kajki 1",
//			"6, Biedronka, Olsztyn, ul. Jerzego Lanca 4",
//			"3, Carrefour Express, Katowice, ul. Mrongowiusza 11"
//	})
//	@DisplayName("PUT: '/api/store/{store_id}' removes beer prices")
//	@DirtiesContext
//	public void replaceStoreRemovesAllPricesTest(Long id, String name, String city, String street) {
//		// given
//		StoreRequestDTO request = createStoreRequest(name, city, street);
//		var getResponse = getRequestAuth("admin", "admin", "/api/store/" + id + "/beer-price");
//		String jsonBeerPrices = getResponse.getBody();
//		List<BeerPriceResponseDTO> beerPrices = toModelList(jsonBeerPrices, BeerPriceResponseDTO.class);
//		assertThat(beerPrices).isNotEmpty();
//
//		// when
//		var putResponse = putRequestAuth("admin", "admin", "/api/store/" + id, request);
//		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//		// then
//		getResponse = getRequestAuth("admin", "admin", "/api/store/" + id + "/beer-price");
//		jsonBeerPrices = getResponse.getBody();
//		beerPrices = toModelList(jsonBeerPrices, BeerPriceResponseDTO.class);
//		assertThat(beerPrices).isEmpty();
//	}
//@ParameterizedTest
//@CsvSource(value = {
//		"2, Lidl, null, null",
//		"4, null, Ilawa, null",
//		"5, null, null, ul. Nowaka 9"
//}, nullValues = "null")
//@DisplayName("PATCH: '/api/store/{store_id}' any change = remove beer price")
//@DirtiesContext
//public void updateAnyStoreFieldRemovesAllPricesTest(Long id, String name, String city, String street) {
//	// given
//	StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
//	var getResponse = getRequestAuth("admin", "admin", "/api/store/" + id + "/beer-price");
//	String jsonBeerPrices = getResponse.getBody();
//	List<BeerPriceResponseDTO> beerPrices = toModelList(jsonBeerPrices, BeerPriceResponseDTO.class);
//	assertThat(beerPrices).isNotEmpty();
//
//	// when
//	var patchResponse = patchRequestAuth("admin", "admin", "/api/store/" + id, request);
//	assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//	// then
//	getResponse = getRequestAuth("admin", "admin", "/api/store/" + id + "/beer-price");
//	jsonBeerPrices = getResponse.getBody();
//	beerPrices = toModelList(jsonBeerPrices, BeerPriceResponseDTO.class);
//	assertThat(beerPrices).isEmpty();
//}
}
