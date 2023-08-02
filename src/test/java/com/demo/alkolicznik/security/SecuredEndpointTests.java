package com.demo.alkolicznik.security;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerDeleteRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
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

		@ParameterizedTest
		@CsvSource({
				"/api/beer"
		})
		@DisplayName("[ANON]: restricted GET endpoints")
		public void anonRestrictedGetEndpointsTest(String endpoint) {
			var response = getRequest(endpoint);
			String actualJson = response.getBody();

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
		@DisplayName("[ANON]: restricted POST endpoints")
		public void anonRestrictedPostEndpointsTest(String endpoint, String brand,
				String type, Double volume) {
			// given
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
				"/api/beer/4, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
				"/api/beer/9218, null, IPA, 0",
				"/api/beer/3, null, null, null"
		}, nullValues = "null")
		@DisplayName("[ANON]: restricted PUT endpoints")
		public void anonRestrictedPutEndpointsTest(String endpoint, String brand,
				String type, Double volume) {
			// given
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
				"/api/beer/2, Miloslaw, null, 0.33",
				"/api/beer/-5, Namyslow, null, -0.5",
				"/api/beer/3, null, null, null"
		}, nullValues = "null")
		@DisplayName("[ANON]: restricted PATCH requests")
		public void anonRestrictedPatchRequestsTest(String endpoint, String brand,
				String type, Double volume) {
			// given
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
				"/api/beer, Ksiazece, Zlote pszeniczne, null",
				"/api/beer, null, Biale, 0",
				"/api/beer, null, null, null"
		}, nullValues = "null")
		@DisplayName("[ANON]: restricted DELETE by object requests")
		public void anonRestrictedDeleteByObjectRequestsTest(String endpoint, String brand,
				String type, Double volume) {
			// given
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
		@CsvSource(value = {
				"/api/beer/1",
				"/api/beer/-5"
		})
		@DisplayName("[ANON]: restricted DELETE by id requests")
		public void anonRestrictedDeleteByIdRequestsTest(String endpoint) {
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
}
