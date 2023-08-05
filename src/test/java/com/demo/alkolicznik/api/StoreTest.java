package com.demo.alkolicznik.api;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.store.StoreDeleteDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Store;
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
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@ActiveProfiles("main")
@TestClassOrder(ClassOrderer.Random.class)
public class StoreTest {

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class GetRequests {

		private final List<Store> stores;

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
		@ValueSource(strings = { "Przemysl", "", "Olecko" })
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
					//.withFailMessage("Elements were not the same or the order of actual array was not ascending by id")
					.containsExactlyElementsOf(expected);
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PostRequests {

		private final List<Store> stores;

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
		@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
		public void createStoreTest(String name, String city, String street) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store",
					createStoreRequest(name, city, street));
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

			String actualJson = postResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			StoreResponseDTO expected = createStoreResponse((stores.size() + 1), name, city, street);
			String expectedJson = toJsonString(expected);

			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);

			var getResponse = getRequest("/api/store/" + (stores.size() + 1));

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
		@CsvSource(value = {
				"null, Reszel, null, Name was not specified; Street was not specified",
				"Lubi, null, null, City was not specified; Street was not specified",
				"null, null, ul. Borowa 1, City was not specified; Name was not specified"
		}, nullValues = "null")
		@DisplayName("POST: '/api/store' [COMBO]")
		public void createStoreNameBlankCityNullStreetEmptyTest(String name, String city, String street, String errorMessage) {
			var postResponse = postRequestAuth("admin", "admin",
					"/api/store", createStoreRequest(name, city, street));

			String json = postResponse.getBody();

			assertIsError(json,
					HttpStatus.BAD_REQUEST,
					errorMessage,
					"/api/store");
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PutRequests {

		private final List<Store> stores;

		@Autowired
		public PutRequests(List<Store> stores) {
			this.stores = stores;
		}

		@ParameterizedTest
		@CsvSource({
				"1, Biedronka, Olsztyn, ul. Borkowskiego 3",
				"7, Grosik, Gdansk, ul. Morska 22",
				"4, Dwojka, Poltusk, ul. Kaczynskiego 13",
				"6, Na Gorce, Mragowo, ul. Parkowa 1"
		})
		@DirtiesContext
		@DisplayName("PUT: '/api/store/{store_id}'")
		public void replaceStoreTest(Long id, String name, String city, String street) {
			// given
			StoreRequestDTO request = createStoreRequest(name, city, street);

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/" + id, request);
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			StoreResponseDTO expected = createStoreResponse(id.intValue(), name, city, street);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// when
			var getResponse = getRequest("/api/store/" + id);
			actualJson = getResponse.getBody();
			actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(longs = { -893, 0, 9999 })
		@DisplayName("PUT: '/api/store/{store_id}' [STORE_NOT_FOUND]")
		public void replaceStoreNotFoundTest(Long id) {
			// given
			StoreRequestDTO request = createStoreRequest("Lewiatan", "Antoninow", "ul. Nowickiego 64");

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/" + id, request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + id + "' id",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@CsvSource(value = {
				"5, Lubi, Warszawa, ul. Nowaka 5",
				"9, Zabka, Ilawa, ul. Dworcowa 3",
				"7, Tesco, Gdansk, ul. Morska 22"
		}, nullValues = "null")
		@DisplayName("PUT: '/api/store/{store_id}' [STORE_EQUALS]")
		public void replaceStorePropertiesSameTest(Long id, String name, String city, String street) {
			// given
			StoreRequestDTO request = createStoreRequest(name, city, street);

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/" + id, request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { "  \n \t", "" })
		@DisplayName("PUT: '/api/store/{store_id}' [NAME_BLANK]")
		public void replaceStoreNameBlankTest(String name) {
			// given
			StoreRequestDTO request = createStoreRequest(name, "Torun", "ul. Przybyszow 2");

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/3", request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.BAD_REQUEST,
					"Name was not specified",
					"/api/store/3");
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { "  \n \t", "" })
		@DisplayName("PUT: '/api/store/{store_id}' [CITY_BLANK]")
		public void replaceStoreCityBlankTest(String city) {
			// given
			StoreRequestDTO request = createStoreRequest("Tesco", city, "ul. Przybyszow 2");

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/7", request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.BAD_REQUEST,
					"City was not specified",
					"/api/store/7");
		}

		@ParameterizedTest
		@NullSource
		@ValueSource(strings = { "  \n \t", "" })
		@DisplayName("PUT: '/api/store/{store_id}' [NAME_BLANK]")
		public void replaceStoreStreetBlankTest(String street) {
			// given
			StoreRequestDTO request = createStoreRequest("Tesco", "Torun", street);

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/1", request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.BAD_REQUEST,
					"Street was not specified",
					"/api/store/1");
		}

		@ParameterizedTest
		@CsvSource(value = {
				"1, Lidl, Olsztyn, ul. Iwaszkiewicza 1",
				"6, Zabka, Ilawa, ul. Dworcowa 3",
				"9, ABC, Warszawa, ul. Zeromskiego 3"
		}, nullValues = "null")
		@DisplayName("PUT: '/api/store/{store_id}' [STORE_EXISTS]")
		public void replaceStoreAlreadyExistsTest(Long id, String name, String city, String street) {
			// given
			StoreRequestDTO request = createStoreRequest(name, city, street);

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/store/" + id, request);
			String actualJson = putResponse.getBody();

			// then
			assertIsError(actualJson,
					HttpStatus.CONFLICT,
					"Store already exists",
					"/api/store/" + id);
		}

	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PatchRequests {

		private List<Store> stores;

		@Autowired
		public PatchRequests(List<Store> stores) {
			this.stores = stores;
		}

		@Test
		@DisplayName("PATCH: '/api/store/{store_id}' update name")
		@DirtiesContext
		public void updateStoreNameTest() {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest("Carrefour Express", null, null);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/1", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			StoreResponseDTO expected = createStoreResponse(1, "Carrefour Express", "Olsztyn", "ul. Barcza 4");
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PATCH: '/api/store/{store_id}' update city")
		@DirtiesContext
		public void updateStoreCityTest() {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest(null, "Gdynia", null);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/7", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			StoreResponseDTO expected = createStoreResponse(7, "Tesco", "Gdynia", "ul. Morska 22");
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PATCH: '/api/store/{store_id}' update street")
		@DirtiesContext
		public void updateStoreStreetTest() {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest(null, null, "ul. Zeromskiego 4");

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/4", request);
			assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = patchResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			StoreResponseDTO expected = createStoreResponse(4, "ABC", "Warszawa", "ul. Zeromskiego 4");
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@ParameterizedTest
		@ValueSource(longs = { 0, -19, 3456 })
		@DisplayName("PATCH: '/api/store/{store_id}' [STORE_NOT_FOUND]")
		public void updateStoreNotFoundTest(Long id) {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest("Lidl", "Bydgoszcz", "al. Wroclawska 9");

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/" + id, request);
			String actualJson = patchResponse.getBody();

			// then
			assertIsError(
					actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + id + "' id",
					"/api/store/" + id
			);
		}

		@ParameterizedTest
		@CsvSource(value = {
				"8, null, null, ul. Barcza 4",
				"3, Biedronka, null, ul. Sikorskiego-Wilczynskiego 12",
				"5, Lidl, Olsztyn, ul. Iwaszkiewicza 1",
				"9, Grosik, Olsztyn, null"
		}, nullValues = "null")
		@DisplayName("PATCH: '/api/store/{store_id}' [STORE_ALREADY_EXISTS]")
		public void updateStoreAlreadyExistsTest(Long id, String name, String city, String street) {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/" + id, request);
			String actualJson = patchResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.CONFLICT,
					"Store already exists",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@CsvSource({
				"7, Tesco, Gdansk, ul. Morska 22",
				"2, Biedronka, Olsztyn, ul. Sikorskiego-Wilczynskiego 12",
				"4, ABC, Warszawa, ul. Zeromskiego 3"
		})
		@DisplayName("PATCH: '/api/store/{store_id}' [PROPERTIES_SAME]")
		public void updateStorePropertiesSameTest(Long id, String name, String city, String street) {
			StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
			var patchResponse = patchRequestAuth("admin", "admin",
					"/api/store/" + id, request);

			String jsonResponse = patchResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.OK,
					"Objects are the same: nothing to update",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@ValueSource(strings = { "", " \t \n" })
		@DisplayName("PATCH: '/api/store/{store_id}' [NAME_BLANK_IF_EXISTS]")
		public void updateStoreNameBlankTest(String name) {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest(name, null, null);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/4", request);
			String actualJson = patchResponse.getBody();

			// then
			assertIsError(
					actualJson,
					HttpStatus.BAD_REQUEST,
					"Name was not specified",
					"/api/store/4"
			);
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "\n\n   \t" })
		@DisplayName("PATCH: '/api/store/{store_id}' [STREET_BLANK_IF_EXISTS]")
		public void updateStoreStreetBlankTest(String street) {
			// given
			StoreUpdateDTO request = createStoreUpdateRequest(null, null, street);

			// when
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/4", request);
			String actualJson = patchResponse.getBody();

			// then
			assertIsError(
					actualJson,
					HttpStatus.BAD_REQUEST,
					"Street was not specified",
					"/api/store/4"
			);
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "\n\n   \t" })
		@DisplayName("PATCH: '/api/store/{store_id}' [CITY_BLANK_IF_EXISTS]")
		public void updateStoreCityBlankTest(String city) {
			StoreUpdateDTO request = createStoreUpdateRequest(null, city, null);
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/7", request);

			String jsonResponse = patchResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"City was not specified",
					"/api/store/7"
			);
		}

		@Test
		@DisplayName("PATCH: '/api/store/{store_id}' [NO_PROPERTIES]")
		public void updateNoPropertiesSpecifiedTest() {
			StoreUpdateDTO request = createStoreUpdateRequest(null, null, null);
			var patchResponse = patchRequestAuth("admin", "admin", "/api/store/2", request);

			String jsonResponse = patchResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.BAD_REQUEST,
					"No property to update was specified",
					"/api/store/2"
			);
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class DeleteRequests {

		private List<Store> stores;

		@Autowired
		public DeleteRequests(List<Store> stores) {
			this.stores = stores;
		}

		@ParameterizedTest
		@ValueSource(longs = { 1, 5 })
		@DisplayName("DELETE: '/api/store/{store_id}'")
		@DirtiesContext
		public void deleteStoreTest(Long id) {
			// when
			var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/" + id);
			assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = deleteResponse.getBody();
			StoreDeleteDTO actual = toModel(actualJson, StoreDeleteDTO.class);

			// then
			StoreDeleteDTO expected = createStoreDeleteResponse(
					getStore(id.longValue(), stores),
					"Store was deleted successfully!"
			);
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);

			// when
			var getResponse = getRequest("/api/store/" + id);

			actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + id + "' id",
					"/api/store/" + id);
		}

		@ParameterizedTest
		@ValueSource(longs = { -14, 0, 849 })
		@DisplayName("DELETE: '/api/store/{store_id}' [STORE_NOT_FOUND]")
		public void deleteStoreNotExistsTest(Long id) {
			var deleteResponse = deleteRequestAuth("admin", "admin",
					"/api/store/" + id);

			String jsonResponse = deleteResponse.getBody();

			assertIsError(
					jsonResponse,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '" + id + "' id",
					"/api/store/" + id
			);
		}
	}
}
