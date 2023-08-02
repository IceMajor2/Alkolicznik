package com.demo.alkolicznik.security;

import java.util.Map;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerDeleteRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.deleteRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.patchRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.putRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("main")
@Import(DisabledVaadinContext.class)
@TestClassOrder(ClassOrderer.Random.class)
public class SecuredEndpointTests {

	@Nested
	@TestClassOrder(ClassOrderer.Random.class)
	class BeerController {

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		class Anonymous {

			@Test
			@DisplayName("[ANON]: restricted GET endpoints")
			public void anonRestrictedGetEndpointsTest() {
				String endpoint = "/api/beer";
				var response = getRequest(endpoint);
				String actualJson = response.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"Ksiazece, IPA, null",
					"'', null, -1.0"
			}, nullValues = "null")
			@DisplayName("[ANON]: restricted POST endpoints")
			public void anonRestrictedPostEndpointsTest(String brand, String type, Double volume) {
				// given
				String endpoint = "/api/beer";
				BeerRequestDTO request = createBeerRequest(brand, type, volume);
				// when
				var response = postRequest(endpoint, request);
				String actualJson = response.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"4, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
					"9218, null, IPA, 0",
					"3, null, null, null"
			}, nullValues = "null")
			@DisplayName("[ANON]: restricted PUT endpoints")
			public void anonRestrictedPutEndpointsTest(Long beerId, String brand,
					String type, Double volume) {
				// given
				String endpoint = "/api/beer" + beerId;
				BeerRequestDTO request = createBeerRequest(brand, type, volume);

				// when
				var response = putRequest(endpoint, request);
				String actualJson = response.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"2, Miloslaw, null, 0.33",
					"-5, Namyslow, null, -0.5",
					"3, null, null, null"
			}, nullValues = "null")
			@DisplayName("[ANON]: restricted PATCH requests")
			public void anonRestrictedPatchRequestsTest(Long beerId, String brand,
					String type, Double volume) {
				// given
				String endpoint = "/api/beer" + beerId;
				BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

				// when
				var response = patchRequest(endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"Ksiazece, Zlote pszeniczne, null",
					"null, Biale, 0",
					"null, null, null"
			}, nullValues = "null")
			@DisplayName("[ANON]: restricted DELETE by object requests")
			public void anonRestrictedDeleteByObjectRequestsTest(String brand, String type,
					Double volume) {
				// given
				String endpoint = "/api/beer";
				BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

				// when
				var response = deleteRequest(endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@ValueSource(longs = {3, -5})
			@DisplayName("[ANON]: restricted DELETE by id requests")
			public void anonRestrictedDeleteByIdRequestsTest(Long beerId) {
				String endpoint = "/api/beer" + beerId;
				// when
				var response = deleteRequest(endpoint);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		class Unauthorized {

			private String username = "user";

			private String password = "user";

			@ParameterizedTest
			@CsvSource({
					"/api/beer"
			})
			@DisplayName("[UNAUTHORIZED]: restricted GET endpoints")
			public void unauthorizedRestrictedEndpointsTest(String endpoint) {
				var response = getRequestAuth(username, password, endpoint);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"/api/beer, Ksiazece, IPA, null",
					"/api/beer, '', null, -1.0"
			}, nullValues = "null")
			@DisplayName("[UNAUTHORIZED]: restricted POST endpoints")
			public void unauthorizedRestrictedPostEndpointsTest(String endpoint, String brand,
					String type, Double volume) {
				// given
				BeerRequestDTO request = createBeerRequest(brand, type, volume);
				// when
				var response = postRequestAuth(username, password, endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"/api/beer/4, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
					"/api/beer/9218, null, IPA, 0",
					"/api/beer/3, null, null, null"
			}, nullValues = "null")
			@DisplayName("[UNAUTHORIZED]: restricted PUT endpoints")
			public void unauthorizedRestrictedPutEndpointsTest(String endpoint, String brand,
					String type, Double volume) {
				// given
				BeerRequestDTO request = createBeerRequest(brand, type, volume);

				// when
				var response = putRequestAuth(username, password, endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"/api/beer/2, Miloslaw, null, 0.33",
					"/api/beer/-5, Namyslow, null, -0.5",
					"/api/beer/3, null, null, null"
			}, nullValues = "null")
			@DisplayName("[UNAUTHORIZED]: restricted PATCH requests")
			public void unauthorizedRestrictedPatchRequestsTest(String endpoint, String brand,
					String type, Double volume) {
				// given
				BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

				// when
				var response = patchRequestAuth(username, password, endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"/api/beer, Ksiazece, Zlote pszeniczne, null",
					"/api/beer, null, Biale, 0",
					"/api/beer, null, null, null"
			}, nullValues = "null")
			@DisplayName("[UNAUTHORIZED]: restricted DELETE by object requests")
			public void unauthorizedRestrictedDeleteByObjectRequestsTest(String endpoint, String brand,
					String type, Double volume) {
				// given
				BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

				// when
				var response = deleteRequestAuth(username, password, endpoint, request);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"/api/beer/1",
					"/api/beer/-5"
			})
			@DisplayName("[UNAUTHORIZED]: restricted DELETE by id requests")
			public void unauthorizedRestrictedDeleteByIdRequestsTest(String endpoint) {
				// when
				var response = deleteRequestAuth(username, password, endpoint);
				String actualJson = response.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Resource not found",
						endpoint);
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		class Authorized {

			// unauthorized
			private Map<String, String> credentials = Map.of(
					"accountant", "accountant",
					"user", "user");


			@ParameterizedTest
			@CsvSource({
					"/api/beer"
			})
			@DisplayName("[AUTHORIZED]: restricted GET endpoints")
			public void authorizedRestrictedEndpointsTest(String endpoint) {
				for(var entry : credentials.entrySet()) {

				}
				var response = getRequestAuth()
			}
		}
	}
}
