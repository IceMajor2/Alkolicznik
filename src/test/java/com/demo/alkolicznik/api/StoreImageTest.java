package com.demo.alkolicznik.api;

import java.util.List;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.getRawPathToImage;
import static com.demo.alkolicznik.utils.TestUtils.getStoreImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "enable.image.database=true")
@Import(DisabledVaadinContext.class)
@ActiveProfiles({ "main", "image" })
@TestClassOrder(ClassOrderer.Random.class)
class StoreImageTest {

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class GetRequests {

		private List<Store> stores;

		@Autowired
		public GetRequests(List<Store> stores) {
			this.stores = stores;
		}

		@ParameterizedTest
		@ValueSource(longs = { 1, 8, 9, 3 })
		@DisplayName("GET: '/api/store/{store_id}/image'")
		public void whenGettingStoreImage_thenReturnOKTest(Long storeId) {
			StoreImage img = getStoreImage(storeId.longValue(), stores);

			// when
			var getResponse = getRequest("/api/store/" + storeId + "/image");
			String actualJson = getResponse.getBody();
			ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

			// then
			ImageModelResponseDTO expected = createImageResponse(img);
			String expectedJson = toJsonString(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);
		}

		@ParameterizedTest
		@ValueSource(longs = { -349, 0, 129048 })
		@DisplayName("GET: '/api/store/{store_id}/image' [STORE_NOT_FOUND]")
		public void shouldReturnNotFoundOnInvalidStoreIdTest(Long storeId) {
			var getResponse = getRequest("/api/store/" + storeId + "/image");

			String actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '%d' id".formatted(storeId),
					"/api/store/" + storeId + "/image"
			);
		}

		@ParameterizedTest
		@ValueSource(longs = { 2, 4, 5 })
		@DisplayName("GET: '/api/store/{store_id}/image' [NO_IMAGE]")
		public void shouldReturnNotFoundOnBeerWithNoImageTest(Long storeId) {
			var getResponse = getRequest("/api/store/" + storeId + "/image");

			String actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find image for this store",
					"/api/store/" + storeId + "/image");
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PostRequests {

		private List<Store> stores;

		@Autowired
		public PostRequests(List<Store> stores) {
			this.stores = stores;
		}

		@ParameterizedTest
		@CsvSource({
				"Auchan, Ostroleka, ul. Cieszynska 3, auchan.webp, auchan-ostroleka-cieszynska.webp",
				"Piotr i Pawel, Rzeszow, ul. Krakowska 12, groszek.png, piotr-i-pawel-rzeszow-krakowska.png",
				"Lewiatan, Zielona Gora, ul. Generala Andersa 15, lewiatan.png, lewiatan-zielona-gora-generala-andersa.png"
		})
		@DisplayName("POST: '/api/store' with new name")
		@DirtiesContext
		public void whenAddingStoreWithImage_thenReturnOKTest(String name, String city,
				String street, String filename, String expectedFilename) {
			// given
			StoreRequestDTO request = createStoreRequest(name, city, street,
					getRawPathToImage("store/" + filename));

			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			String actualJson = postResponse.getBody();
			StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);

			// then
			assertThat(actual.getImage().getImageUrl())
					.withFailMessage("The image's remote id was null. That means it "
							+ "probably was not send to the remote server.")
					.isNotNull();
			StoreResponseDTO expected = createStoreResponse(stores.size() + 1, name,
					city, street, createImageResponse(expectedFilename, actual.getImage()));
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

//		@ParameterizedTest
//		@CsvSource({
//				""
//		})
//		@DisplayName("POST: '/api/store' with new name")
//		@DirtiesContext
//		public void whenAddingStoreWithImage
	}
}