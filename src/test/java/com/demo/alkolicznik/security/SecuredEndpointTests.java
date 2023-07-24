package com.demo.alkolicznik.security;

import java.util.List;
import java.util.Map;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceUpdateDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerPriceUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreUpdateRequest;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.deleteRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.putRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("main")
public class SecuredEndpointTests {

	@Autowired
	private List<Beer> beers;

	@Autowired
	private List<Store> stores;

	@Nested
	class BeerController {

		@Nested
		class Anonymous {

			@Test
			@DisplayName("ANONYMOUS: GET '/api/beer'")
			public void whenAnonGetsBeers_thenReturn404Test() {
				var getResponse = getRequest("/api/beer");

				String json = getResponse.getBody();

				assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/beer");
			}

//			@Test
//			@DisplayName("POST: '/api/beer' [INVALID_BODY; UNAUTHORIZED]")
//			public void givenInvalidRequest_whenUserIsUnauthorized_thenReturn404Test() {
//				var postResponse = postRequest("/api/beer", createBeerRequest(null, null, null));
//
//				String jsonResponse = postResponse.getBody();
//
//				assertIsError(jsonResponse,
//						HttpStatus.NOT_FOUND,
//						"Resource not found",
//						"/api/beer");
//			}

			@Test
			@DisplayName("ANONYMOUS: POST '/api/beer'")
			public void whenAnonCreatesBeer_thenReturn404Test() {
				BeerRequestDTO request = createBeerRequest(beers.get(1));
				var postResponse = postRequest("/api/beer", request);

				String json = postResponse.getBody();

				assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/beer");
			}

			@Test
			@DisplayName("ANONYMOUS: PUT '/api/beer/{beer_id}'")
			public void whenAnonUpdatesBeer_thenReturn404Test() {
				BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null, null);
				var putResponse = putRequest("/api/beer/2", request);

				String jsonResponse = putResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer/2"
				);
			}

			@Test
			@DisplayName("ANONYMOUS: DELETE '/api/beer/{beer_id}' (params)")
			public void whenAnonDeletesBeer_thenReturn404Test() {
				var deleteResponse = deleteRequest("/api/beer/2");

				String jsonResponse = deleteResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer/2"
				);
			}

			@Test
			@DisplayName("ANONYMOUS: DELETE '/api/beer' (object)")
			public void whenAnonDeletesBeerByFields_thenReturn404Test() {
				BeerRequestDTO request = createBeerRequest(getBeer(3L, beers));
				var deleteResponse = deleteRequest("/api/beer", request);

				String jsonResponse = deleteResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer"
				);
			}
		}

		@Nested
		class User {

			@Test
			@DisplayName("USER: GET '/api/beer'")
			public void whenUserGetsBeers_thenReturn404Test() {
				var getResponse = getRequestAuth("user", "user", "/api/beer");

				String json = getResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer");
			}

			@Test
			@DisplayName("USER: POST '/api/beer'")
			public void whenUserCreatesBeers_thenReturn404Test() {
				BeerRequestDTO request = createBeerRequest(beers.get(1));
				var postResponse = postRequestAuth("user", "user", "/api/beer", request);

				String json = postResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer");
			}

			@Test
			@DisplayName("USER: PUT '/api/beer/{beer_id}'")
			public void whenUserUpdatesBeer_thenReturn404Test() {
				BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null, null);
				var putResponse = putRequestAuth("user", "user", "/api/beer/1", request);

				String json = putResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer/1");
			}

//			@Test
//			@DisplayName("USER: PATCH '/api/beer/{beer_id}' [INVALID_REQUEST]")
//			public void givenInvalidBody_whenUserIsUnauthorized_thenReturn404Test() {
//				var putResponse = patchRequestAuth("user", "user", "/api/beer/5",
//						createBeerUpdateRequest(" ", "Porter Malinowy", -1d, null));
//
//				String jsonResponse = putResponse.getBody();
//
//				assertIsError(
//						jsonResponse,
//						HttpStatus.NOT_FOUND,
//						"Resource not found",
//						"/api/beer/5"
//				);
//			}

			@Test
			@DisplayName("USER: DELETE '/api/beer/{beer_id}' (params)")
			public void whenUserDeletesBeer_thenReturn404Test() {
				var deleteResponse = deleteRequestAuth("user", "user",
						"/api/beer/1");

				String json = deleteResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer/1");
			}

			@Test
			@DisplayName("USER: DELETE '/api/beer' (object)")
			public void whenUserDeletesBeerByFields_thenReturn404Test() {
				BeerRequestDTO request = createBeerRequest(beers.get(1));
				var deleteResponse = deleteRequestAuth("user", "user", "/api/beer", request);

				String json = deleteResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer");
			}
		}

		@Nested
		class Accountant {

			@Test
			@DisplayName("ACCOUNTANT: GET '/api/beer'")
			public void whenAccountantGetsBeers_thenReturn200Test() {
				var getResponse = getRequestAuth("accountant", "accountant", "/api/beer");
				assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			}

			@Test
			@DisplayName("ACCOUNTANT: POST '/api/beer'")
			public void whenAccountantCreatesBeer_thenReturn201Test() {
				BeerRequestDTO request = createBeerRequest("Ksiazece", "Wisniowe", null);
				var postResponse = postRequestAuth("accountant", "accountant", "/api/beer", request);
				assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			}

			@Test
			@DisplayName("ACCOUNTANT: PATCH '/api/beer/{beer_id}'")
			public void whenAccountantPatchesBeer_thenReturn204Test() {
				BeerUpdateDTO request = createBeerUpdateRequest(null, "", null);
				var putResponse = patchRequestAuth("accountant", "accountant", "/api/beer/1", request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			}

			@Test
			@DisplayName("ACCOUNTANT: DELETE '/api/beer/{beer_id}' (params)")
			public void whenAccountantDeletesBeer_thenReturn200Test() {
				var deleteResponse = deleteRequestAuth("accountant", "accountant", "/api/beer/6");
				assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			}

			@Test
			@DisplayName("ACCOUNTANT: DELETE '/api/beer' (object)")
			public void whenAccountantDeletesBeerByFields_thenReturn200Test() {
				BeerRequestDTO request = createBeerRequest(beers.get(1));
				var deleteResponse = deleteRequestAuth("accountant", "accountant", "/api/beer", request);
				assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			}
		}

		@Nested
		class Admin {

			//        @Test
//        @DisplayName("ADMIN: get beers")
//        @Test
//        @DisplayName("ADMIN: creates beer")
//        public void whenAdminCreatesBeer_thenReturn404Test() {
//
//        }
			//        @Test
//        @DisplayName("ADMIN: update beer")

			//        @Test
//        @DisplayName("ADMIN: delete beer (ID)")

			//        @Test
//        @DisplayName("ADMIN: delete beer (fields)")
		}
	}

	@Nested
	class StoreController {

		@Nested
		class Anonymous {

			@Test
			@DisplayName("ANONYMOUS: GET '/api/store'")
			public void whenAnonGetsStores_thenReturn404Test() {
				var getResponse = getRequest("/api/store");

				String json = getResponse.getBody();

				assertIsError(json, HttpStatus.NOT_FOUND, "Resource not found", "/api/store");
			}

			@Test
			@DisplayName("ANONYMOUS: PUT '/api/store/{store_id}'")
			public void whenAnonUpdatesStore_thenReturn404Test() {
				StoreUpdateDTO request = createStoreUpdateRequest("Lubi", null, null);
				var putResponse = putRequestAuth("user", "user", "/api/store/4", request);

				String jsonResponse = putResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/store/4"
				);
			}

			@Test
			@DisplayName("ANONYMOUS: DELETE '/api/store/{store_id}'")
			public void whenAnonDeletesStore_thenReturn404Test() {
				var deleteResponse = deleteRequestAuth("user", "user",
						"/api/store/2");

				String jsonResponse = deleteResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/store/2"
				);
			}
		}

		@Nested
		class User {

			//        @Test
//        @DisplayName("USER: get stores")

			//        @Test
//        @DisplayName("USER: update store")

			//        @Test
//        @DisplayName("USER: delete store")
		}

		@Nested
		class Accountant {

			//        @Test
//        @DisplayName("ACCOUNTANT: get stores")

			//        @Test
//        @DisplayName("ACCOUNTANT: update store")

			//        @Test
//        @DisplayName("ACCOUNTANT: delete store")
		}

		@Nested
		class Admin {

			//        @Test
//        @DisplayName("ADMIN: get stores")

			//        @Test
//        @DisplayName("ADMIN: update store")

			//        @Test
//        @DisplayName("ADMIN: delete store")

		}

	}

	@Nested
	class BeerPriceController {

		@Nested
		class Anonymous {

			@Test
			@DisplayName("ANONYMOUS: GET '/api/beer-price'")
			public void whenAnonGetsPrices_thenReturn404Test() {
				var getResponse = getRequest("/api/beer-price");

				String json = getResponse.getBody();

				assertIsError(json,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer-price");
			}

			//        @Test
//        @DisplayName("ANONYMOUS: create price by JSON")

			//        @Test
//        @DisplayName("ANONYMOUS: create price by URL parameters")

			@Test
			@DisplayName("ANONYMOUS: PUT '/api/beer-price'")
			public void whenAnonUpdatesPrice_thenReturn404Test() {
				BeerPriceUpdateDTO request = createBeerPriceUpdateRequest(7.89);
				var putResponse = putRequestAuth("user", "user",
						"/api/beer-price", request, Map.of("beer_id", 3L, "store_id", 2L));

				String jsonResponse = putResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer-price"
				);
			}

			@Test
			@DisplayName("ANONYMOUS: DELETE '/api/beer-price'")
			public void whenAnonDeletesPrice_thenReturn404Test() {

				var deleteResponse = deleteRequestAuth("user", "user",
						"/api/beer-price", Map.of("beer_id", 2L, "store_id", 5L));

				String jsonResponse = deleteResponse.getBody();

				assertIsError(
						jsonResponse,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						"/api/beer-price"
				);
			}
		}

		@Nested
		class User {

			//        @Test
//        @DisplayName("USER: update store")

			//        @Test
//        @DisplayName("USER: delete store")

			//        @Test
//        @DisplayName("USER: create price by JSON")

			//        @Test
//        @DisplayName("USER: create price by URL parameters")
		}

		@Nested
		class Accountant {

			//        @Test
//        @DisplayName("ACCOUNTANT: get stores")

			//        @Test
//        @DisplayName("ACCOUNTANT: update store")

			//        @Test
//        @DisplayName("ACCOUNTANT: delete store")

			//        @Test
//        @DisplayName("ACCOUNTANT: create price by JSON")

			//        @Test
//        @DisplayName("ACCOUNTANT: create price by URL parameters")

		}

		@Nested
		class Admin {

			//        @Test
//        @DisplayName("ADMIN: get stores")

			//        @Test
//        @DisplayName("ADMIN: update store")

			//        @Test
//        @DisplayName("ADMIN: delete store")

			//        @Test
//        @DisplayName("ADMIN: create price by JSON")

			//        @Test
//        @DisplayName("ADMIN: create price by URL parameters")
		}
	}
}
