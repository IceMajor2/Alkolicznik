package com.demo.alkolicznik.api;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.jdbc.core.JdbcTemplate;
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
public class StoreImageTest {

	public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/icemajor/tr:n-get_store/test/store/";

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

		@ParameterizedTest
		@ValueSource(strings = { "Carrefour", "Lidl", "Zabka" })
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
		@ValueSource(strings = { "Biedronka", "Grosik", "Lubi" })
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

		private JdbcTemplate jdbcTemplate;

		@Autowired
		public PostRequests(List<Store> stores, List<StoreImage> storeImages, DataSource dataSource) {
			this.stores = stores;
			this.storeImages = storeImages;
			this.jdbcTemplate = new JdbcTemplate(dataSource);
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
			// given
			StoreImage notExpected = getStoreImage("Zabka", storeImages);
			String notExpectedRemoteId = notExpected.getRemoteId();
//			Image notExpectedImage = notExpected.getImageComponent();
			StoreRequestDTO request = createStoreRequest("Zabka", "Kasztanowo", "ul. Niewiadoma 1",
					getRawPathToImage("store/zabka.jpg"));

			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/store", request);
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			String actualRemoteId = jdbcTemplate.queryForObject(
					"SELECT remote_id FROM store_image WHERE store_name = 'Zabka'", String.class);
//			Image actualImage = new Image(jdbcTemplate.queryForObject
//					("SELECT image_url FROM store_image WHERE store_name = 'Zabka'", String.class),
//					"No image");

			// then
			assertThat(actualRemoteId)
					.withFailMessage("The image url was null. That probably means it "
							+ "was not send to the remote server.")
					.isNotEqualTo(notExpectedRemoteId);
//			assertThat(actualImage).isNotEqualTo(notExpectedImage);
		}

		@ParameterizedTest
		@CsvSource
		@DisplayName("POST: '/api/store' [STORE_EXISTS]; only image is different")
		@DirtiesContext
		public void shouldReturn409WhenBodyHasOnlyDifferentImageValueTest() {

		}

		@ParameterizedTest
		@CsvSource
		@DisplayName("POST: '/api/store' image is connected to new store")
		@DirtiesContext
		public void imagesShouldBeAssociatedWithNewStoreTest() {

		}

		@ParameterizedTest
		@CsvSource
		@DisplayName("POST: '/api/store' no image for names = no image for new store")
		@DirtiesContext
		public void newStoreWithNoNameImageRelationShouldHaveNoImageTest() {

		}
	}
}