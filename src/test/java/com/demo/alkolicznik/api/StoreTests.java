package com.demo.alkolicznik.api;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.utils.requests.MockRequests;
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
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreDeleteResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.JsonUtils.toModelList;
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.MockRequests.mockPutRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.putRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@ActiveProfiles("main")
@TestClassOrder(ClassOrderer.Random.class)
public class StoreTests {

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	class GetRequests {

		private List<Store> stores;

		@Autowired
		public GetRequests(List<Store> stores) {
			this.stores = stores;
		}

		@Test
		@DisplayName("GET: '/api/store/{store_id}'")
		public void getStoreTest() {
			var getResponse = getRequest("/api/store/3");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String actualJson = getResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			StoreResponseDTO expected = createStoreResponse(3, "Lidl", "Olsztyn", "ul. Iwaszkiewicza 1");
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(longs = { -11, 0, 9998 })
		@DisplayName("GET: '/api/store/{store_id}' [STORE_NOT_FOUND]")
		public void getStoreNotExistingTest(Long id) {
			var getResponse = getRequest("/api/store/" + id);

			String json = getResponse.getBody();

			assertIsError(json,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + id + "' id",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@ValueSource(strings = { "Warszawa", "Olsztyn", "Gdansk" })
		@DisplayName("GET: '/api/store' of city")
		public void getStoreFromCityArrayTest(String city) {
			var getResponse = getRequest("/api/store", Map.of("city", city));
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

			String jsonResponse = getResponse.getBody();
			List<StoreResponseDTO> actual = toModelList(jsonResponse, StoreResponseDTO.class);

			List<StoreResponseDTO> expected = stores.stream()
					.filter(store -> store.getCity().equals(city))
					.map(StoreResponseDTO::new)
					.toList();
			assertThat(actual)
					.withFailMessage("Elements were not the same or the order of actual array was not ascending by id")
					.containsExactlyElementsOf(expected);
		}

		@ParameterizedTest
		@ValueSource(strings = { "Przemysl", " ", "Olecko" })
		@DisplayName("GET: '/api/store/{store_id}' of city [CITY_NOT_FOUND]")
		public void getStoreFromCityNotExistsArrayTest(String city) {
			var getResponse = getRequest("/api/store", Map.of("city", city));

			String jsonResponse = getResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"No such city: '" + city + "'",
					"/api/store");
		}

		@Test
		@DisplayName("GET: '/api/store' order id asc")
		public void getStoresAllTest() {
			List<StoreResponseDTO> expected = stores.stream()
					.map(StoreResponseDTO::new)
					.sorted(Comparator.comparing(StoreResponseDTO::getId))
					.toList();

			var getResponse = getRequestAuth("admin", "admin", "/api/store");
			assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = getResponse.getBody();
			List<StoreResponseDTO> actual = toModelList(actualJson, StoreResponseDTO.class);

			assertThat(actual)
					.withFailMessage("Elements were not the same or the order of actual array was not ascending by id")
					.containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	class PostRequests {

		private List<Store> stores;

		@Autowired
		public PostRequests(List<Store> stores) {
			this.stores = stores;
		}

		@ParameterizedTest
		@CsvSource({
				"Carrefour, Torun, al. Piernikowa 11",
				"Primo, Olsztyn, ul. Okulickiego 3",
				"Lewiatan, Sopot, ul. Monciaka 2"
		})
		@DisplayName("POST: '/api/store'")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void createStoreTest(String name, String city, String street) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store",
					createStoreRequest(name, city, street));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			StoreResponseDTO expected = createStoreResponse(stores.size() + 1, name, city, street);
			String expectedJson = toJsonString(expected);

			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/store/" + stores.size() + 1);

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { " \n \t", "" })
		@DisplayName("POST: '/api/store' [NAME_BLANK]")
		public void createStoreNameNullTest(String name) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store", createStoreRequest(name, "Mragowo", "ul. Wyspianskiego 17"));

			String json = postResponse.getBody();

			assertIsError(json, HttpStatus.BAD_REQUEST, "Name was not specified", "/api/store");
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { "\t\n", "" })
		@DisplayName("POST: '/api/store' [CITY_BLANK]")
		public void createStoreCityNullTest(String city) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store",
					createStoreRequest("Lubi", city, "ul. Kwiatkowa 3"));

			String json = postResponse.getBody();

			assertIsError(json, HttpStatus.BAD_REQUEST, "City was not specified", "/api/store");
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { " \n  \t", "" })
		@DisplayName("POST: '/api/store' [STREET_BLANK]")
		public void createStoreStreetNullTest(String street) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store",
					createStoreRequest("Primo", "Olsztyn", street));

			String json = postResponse.getBody();

			assertIsError(json, HttpStatus.BAD_REQUEST, "Street was not specified", "/api/store");
		}

		@ParameterizedTest
		@CsvSource({
				"ABC, Warszawa, ul. Zeromskiego 3",
				"Lidl, Olsztyn, ul. Iwaszkiewicza 1",
				"Tesco, Gdansk, ul. Morska 22"
		})
		@DisplayName("POST: '/api/store' [STORE_EXISTS]")
		public void createStoreAlreadyExistsTest(String name, String city, String street) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store", createStoreRequest(name, city, street));

			String json = postResponse.getBody();

			assertIsError(json, HttpStatus.CONFLICT, "Store already exists", "/api/store");
		}

		@ParameterizedTest
		@CsvSource({
				"null, Reszel, null, Name was not specified; Street was not specified",
				"Lubi, null, null, City was not specified; Street was not specified",
				"null, null, ul. Borowa 1, City was not specified; Name was not specified"
		})
		@DisplayName("POST: '/api/store' [COMBO]")
		public void createStoreNameBlankCityNullStreetEmptyTest(String name, String city, String street, String errorMessage) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store",
					createStoreRequest(name, city, street));

			String json = postResponse.getBody();
			System.out.println(json);
			assertIsError(json,
					HttpStatus.BAD_REQUEST,
					errorMessage,
					"/api/store");
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	class PutRequests {

		private List<Store> stores;

		@Autowired
		public PutRequests(List<Store> stores) {
			this.stores = stores;
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' update name")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
		public void updateStoreNameTest() {
			StoreUpdateDTO request = createStoreUpdateRequest("Carrefour Express", null, null);

			StoreResponseDTO expected = createStoreResponse(1, "Carrefour Express", "Olsztyn", "ul. Barcza 4");
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(mockPutRequest("/api/store/1", request),
					HttpStatus.NO_CONTENT,
					expectedJson);
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/store/1");

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' update city")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)

		public void updateStoreCityTest() {
			StoreUpdateDTO request = createStoreUpdateRequest(null, "Gdynia", null);

			StoreResponseDTO expected = createStoreResponse(7, "Tesco", "Gdynia", "ul. Morska 22");
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(mockPutRequest("/api/store/7", request),
					HttpStatus.NO_CONTENT,
					expectedJson);
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/store/7");

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' update street")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)

		public void updateStoreStreetTest() {
			StoreUpdateDTO request = createStoreUpdateRequest(null, null, "ul. Zeromskiego 4");

			StoreResponseDTO expected = createStoreResponse(4, "ABC", "Warszawa", "ul. Zeromskiego 4");
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(mockPutRequest("/api/store/4", request),
					HttpStatus.NO_CONTENT,
					expectedJson);
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/store/4");

			actualJson = getResponse.getBody();
			actual = toModel(actualJson, StoreResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' [NAME_BLANK]")
		public void updateStoreNameBlankTest() {
			StoreUpdateDTO request = createStoreUpdateRequest("", null, null);
			var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Name was not specified",
					"/api/store/4"
			);

			request = createStoreUpdateRequest("\t\n ", null, null);
			putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Name was not specified",
					"/api/store/4"
			);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' [PROPERTIES_SAME]")
		public void updateStorePropertiesSameTest() {
			StoreUpdateDTO request = createStoreUpdateRequest("Lubi", "Warszawa", "ul. Nowaka 5");
			var putResponse = putRequestAuth("admin", "admin",
					"/api/store/5", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/store/5");
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' [STREET_BLANK]")
		public void updateStoreStreetBlankTest() {
			StoreUpdateDTO request = createStoreUpdateRequest(null, null, "");
			var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Street was not specified",
					"/api/store/4"
			);

			request = createStoreUpdateRequest(null, null, "\t\n ");
			putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Street was not specified",
					"/api/store/4"
			);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' [CITY_BLANK]")
		public void updateStoreCityBlankTest() {
			StoreUpdateDTO request = createStoreUpdateRequest(null, "", null);
			var putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			String jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"City was not specified",
					"/api/store/4"
			);

			request = createStoreUpdateRequest(null, "\t\n ", null);
			putResponse = putRequestAuth("admin", "admin", "/api/store/4", request);

			jsonResponse = putResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"City was not specified",
					"/api/store/4"
			);
		}

		@Test
		@DisplayName("PUT: '/api/store/{store_id}' [INVALID_REQUEST; UNAUTHORIZED]")
		public void givenInvalidBody_whenUserIsUnauthorized_thenReturn404Test() {
			var postResponse = putRequest("/api/store/3",
					createStoreUpdateRequest(" ", null, ""));

			String jsonResponse = postResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Resource not found",
					"/api/store/3"
			);
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	class DeleteRequests {

		private List<Store> stores;

		@Autowired
		public DeleteRequests(List<Store> stores) {
			this.stores = stores;
		}

		@Test
		@DisplayName("DELETE: '/api/store/{store_id}'")
		@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)

		public void deleteStoreTest() {
			StoreDeleteDTO expected = createStoreDeleteResponse(
					getStore(6L, stores),
					"Store was deleted successfully!"
			);
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(MockRequests.mockDeleteRequest("/api/store/6"),
					HttpStatus.OK,
					expectedJson);
			assertThat(actualJson).isEqualTo(expectedJson);

			var getRequest = getRequest("/api/store/6");

			String jsonResponse = getRequest.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '6' id",
					"/api/store/6");
		}

		@Test
		@DisplayName("DELETE: '/api/store/{store_id}' [STORE_NOT_FOUND]")
		public void deleteStoreNotExistsTest() {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/store/0");

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '0' id",
					"/api/store/0"
			);
		}
	}
}
