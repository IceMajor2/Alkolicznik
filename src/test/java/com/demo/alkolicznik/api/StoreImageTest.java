package com.demo.alkolicznik.api;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.matchers.BufferedImageAssert;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.createImageRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createStoreResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.getBufferedImageFromLocal;
import static com.demo.alkolicznik.utils.TestUtils.getBufferedImageFromWeb;
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
public class StoreImageTest {

	public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/alkolicznik/test/store/";

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class GetRequests {

		private List<Store> stores;

		private List<StoreImage> storeImages;

		@Autowired
		public GetRequests(List<Store> stores, List<StoreImage> storeImages) {
			this.stores = stores;
			this.storeImages = storeImages;
		}

		@ParameterizedTest
		@ValueSource(longs = { 1, 4, 5, 8 })
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
		@ValueSource(longs = { 2, 3, 7 })
		@DisplayName("GET: '/api/store/{store_id}/image' [NO_IMAGE]")
		public void shouldReturnNotFoundOnBeerWithNoImageTest(Long storeId) {
			var getResponse = getRequest("/api/store/" + storeId + "/image");

			String actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find image for this store",
					"/api/store/" + storeId + "/image");
		}

		@ParameterizedTest
		@ValueSource(strings = { "Carrefour", "ABC", "Lubi" })
		@DisplayName("GET: '/api/image?store_name=?'")
		public void shouldReturnImageTest(String storeName) {
			StoreImage image = getStoreImage(storeName, storeImages);

			// when
			var getResponse = getRequest("/api/image", Map.of("store_name", storeName));
			String actualJson = getResponse.getBody();
			ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

			// then
			ImageModelResponseDTO expected = createImageResponse(image);
			String expectedJson = toJsonString(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);
		}

		@ParameterizedTest
		@ValueSource(strings = { "ikjgsde", "032ka", "fsdkasfgd" })
		@DisplayName("GET: '/api/image?store_name=?' [STORE_NOT_FOUND]")
		public void shouldReturn404OnNameNotFoundTest(String storeName) {
			var getResponse = getRequest("/api/image", Map.of("store_name", storeName));

			String actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find store of '%s' name".formatted(storeName),
					"/api/image"
			);
		}

		@ParameterizedTest
		@ValueSource(strings = { "Biedronka", "Grosik", "Lidl" })
		@DisplayName("GET: '/api/image?store_name=?' [NO_IMAGE]")
		public void shouldReturn404OnNoImageTest(String storeName) {
			var getResponse = getRequest("/api/image", Map.of("store_name", storeName));

			String actualJson = getResponse.getBody();

			assertIsError(actualJson,
					HttpStatus.NOT_FOUND,
					"Unable to find image for this store",
					"/api/image");
		}
	}

	@Nested
	@TestMethodOrder(MethodOrderer.Random.class)
	@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
	class PostRequests {

		private List<Store> stores;

		private List<StoreImage> storeImages;

		@Autowired
		public PostRequests(List<Store> stores, List<StoreImage> storeImages) {
			this.stores = stores;
			this.storeImages = storeImages;
		}

		@ParameterizedTest
		@CsvSource({
				"Auchan, Ostroleka, ul. Cieszynska 3, f_auchan.webp, auchan.webp",
				"Piotr i Pawel, Rzeszow, ul. Krakowska 12, f_piotr-i-pawel.png, piotr-i-pawel.png",
				"Lewiatan, Zielona Gora, ul. Generala Andersa 15, f_lewiatan.png, lewiatan.png"
		})
		@DisplayName("POST: '/api/store' new store name + image at once")
		@DirtiesContext
		public void shouldAddNewStoreWithImageTest(String name, String city,
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
					.withFailMessage("The image url was null. That means it "
							+ "probably was not send to the remote server.")
					.isNotNull();
			StoreResponseDTO expected = createStoreResponse(stores.size() + 1, name,
					city, street, createImageResponse(expectedFilename, actual.getImage(),
							StoreImage.class));
			String expectedJson = toJsonString(expected);
			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/store' new image and new store but with existing name overrides previous image")
		@DirtiesContext
		public void newStoreAndImageButNonUniqueNameShouldAddImageTest() {
			String pathToNewImage = getRawPathToImage("store/f_lubi.jpg");
			String urlToCurrentImage = getStoreImage("Lubi", storeImages).getImageUrl();
			// given
			BufferedImage initial_notExpected = getBufferedImageFromWeb(urlToCurrentImage);
			BufferedImage expected = getBufferedImageFromLocal(pathToNewImage);

			StoreRequestDTO request = createStoreRequest
					("Lubi", "Kasztanowo", "ul. Niewiadoma 1", pathToNewImage);

			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			String actualJson = postResponse.getBody();
			StoreResponseDTO actualResponseDTO = toModel(actualJson, StoreResponseDTO.class);
			BufferedImage actual = getBufferedImageFromWeb(actualResponseDTO.getImage().getImageUrl());

			// then
			BufferedImageAssert.assertThat(actual).isNotEqualTo(initial_notExpected);
			BufferedImageAssert.assertThat(actual).isEqualTo(expected);
		}

		@ParameterizedTest
		@CsvSource({
				"Lubi, Warszawa, ul. Nowaka 5, f_lubi.jpg",
				"Carrefour, Olsztyn, ul. Borkowskiego 3, f_carrefour.jpg"
		})
		@DisplayName("POST: '/api/store' [STORE_EXISTS] with image differing")
		public void shouldReturn409WhenBodyHasOnlyDifferentImageValueTest(String name, String city, String street, String imageFile) {
			// given
			String pathToNewImage = getRawPathToImage("store/" + imageFile);
			StoreRequestDTO request = createStoreRequest(name, city, street, pathToNewImage);
			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);
			// then
			assertIsError(postResponse.getBody(),
					HttpStatus.CONFLICT,
					"Store already exists",
					"/api/store");
		}

		@ParameterizedTest
		@CsvSource({
				"ABC, Kortumowo, ul. Nienackiego 15",
				"Lubi, Malbork, ul. Zamkowa 1",
				"Carrefour, Giby, al. Harcerzow 333"
		})
		@DisplayName("POST: '/api/store' no image in request should not delete previous one")
		@DirtiesContext
		public void noImageInRequestShouldNotDeletePreviousOneTest(String name, String city, String street) {
			String urlToCurrentImage = getStoreImage(name, storeImages)
					.getImageUrl();
			BufferedImage expected = getBufferedImageFromWeb(urlToCurrentImage);
			// given
			StoreRequestDTO request = createStoreRequest(name, city, street);
			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);
			// then
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			StoreResponseDTO actualResponse = toModel(postResponse.getBody(), StoreResponseDTO.class);
			assertThat(actualResponse.getImage())
					.withFailMessage("The image was not found in the response")
					.isNotNull();
			BufferedImage actual = getBufferedImageFromWeb(actualResponse.getImage().getImageUrl());
			BufferedImageAssert.assertThat(actual).isEqualTo(expected);
		}

		@ParameterizedTest
		@CsvSource({
				"Tesco, Wroclaw, ul. Wroclawska 1",
				"Grosik, Olsztyn, ul. Staromiejska 11",
				"Lidl, Rzeszow, ul. Polna 15"
		})
		@DisplayName("POST: '/api/store' no image for brand = no image for new store")
		@DirtiesContext
		public void newStoreWithNoNameImageRelationShouldHaveNoImageTest(String name, String city, String street) {
			StoreRequestDTO request = createStoreRequest(name, city, street);

			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);

			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			StoreResponseDTO actual = toModel(postResponse.getBody(), StoreResponseDTO.class);
			assertThat(actual.getImage()).isNull();
		}

		@ParameterizedTest
		@CsvSource({
				"Intermarche, f_intermarche.webp",
				"Groszek, f_groszek.png",
				"Piotr i Pawel, f_piotr-i-pawel.png"
		})
		@DisplayName("POST: '/api/image?store_name=' [STORE_NOT_FOUND]")
		public void doNotAddImageIfStoreOfNameIsNotFoundTest(String storeName, String imageFile) {
			// given
			ImageRequestDTO request = createImageRequest(getRawPathToImage("store/" + imageFile));
			// when
			var postResponse = postRequestAuth
					("admin", "admin", "/api/image", request, Map.of("store_name", storeName));
			// then
			assertIsError(postResponse.getBody(),
					HttpStatus.NOT_FOUND,
					"Unable to find store of '%s' name".formatted(storeName),
					"/api/image");
		}

		@ParameterizedTest
		@CsvSource({
				"ABC, f_lewiatan.png",
				"Lubi, f_lubi.jpg",
				"Carrefour, f_carrefour.jpg"
		})
		@DisplayName("POST: '/api/image?store_name=' [IMAGE_ALREADY_EXISTS]")
		public void shouldReturn409WhenStoreAlreadyHasImageTest(String storeName, String imageFile) {
			// given
			ImageRequestDTO request = createImageRequest(getRawPathToImage("store/" + imageFile));
			// when
			var postResponse = postRequestAuth
					("admin", "admin", "/api/image", request, Map.of("store_name", storeName));
			// then
			assertIsError(postResponse.getBody(),
					HttpStatus.CONFLICT,
					"Store already has an image.".formatted(storeName),
					"/api/image");
		}
	}
}