package com.demo.alkolicznik.api;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
import static com.demo.alkolicznik.utils.TestUtils.getStore;
import static com.demo.alkolicznik.utils.TestUtils.getStoreImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
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
	@TestClassOrder(ClassOrderer.Random.class)
	class ImageAPI {

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class GetRequests {

			private List<StoreImage> storeImages;

			@Autowired
			public GetRequests(List<StoreImage> storeImages) {
				this.storeImages = storeImages;
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
				ImageModelResponseDTO expected = createImageResponse(image, storeName);
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
						"Store already has an image".formatted(storeName),
						"/api/image");
			}

			@ParameterizedTest
			@CsvSource({
					"Biedronka, f_biedronka.png",
					"Zabka, f_zabka.jpg"
			})
			@DisplayName("POST: '/api/image?store_name=' compare uploaded file with remote")
			@DirtiesContext
			public void uploadedFileShouldMatchRemoteTest(String storeName, String imageFile) {
				// given
				String pathToNewImg = getRawPathToImage("store/" + imageFile);
				ImageRequestDTO request = createImageRequest(pathToNewImg);
				// when
				var postResponse = postRequestAuth
						("admin", "admin", "/api/image", request, Map.of("store_name", storeName));
				assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
				var getResponse = getRequest("/api/image", Map.of("store_name", storeName));
				ImageModelResponseDTO actualResponse = toModel
						(getResponse.getBody(), ImageModelResponseDTO.class);
				// then
				BufferedImage expected = getBufferedImageFromLocal(pathToNewImg);
				BufferedImage actual = getBufferedImageFromWeb(actualResponse.getImageUrl());
				// NOTE: ImageKit compresses images if their quality is too high,
				// thus increasing the chance of a false negative. Here, I'm comparing
				// just the dimensions of the images. It is also flawed as it
				// increases the chance of a false positive.
				BufferedImageAssert.assertThat(actual).hasSameDimensionsAs(expected);
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PatchRequests {

			private List<StoreImage> storeImages;

			@Autowired
			public PatchRequests(List<StoreImage> storeImages) {
				this.storeImages = storeImages;
			}

			@ParameterizedTest
			@CsvSource({
					"Carrefour, f_carrefour.jpg",
					"Lubi, f_lubi.jpg"
			})
			@DisplayName("PATCH: '/api/image?store_name=' successful replacement with image")
			@DirtiesContext
			public void successfulReplacementOfImageEntityTest(String storeName, String imgFile) {
				StoreImage prevImg = getStoreImage(storeName, storeImages);
				BufferedImage prevImgComponent = getBufferedImageFromWeb(prevImg.getImageUrl());
				// given
				ImageRequestDTO request = createImageRequest(getRawPathToImage("store/" + imgFile));
				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/image", request,
						Map.of("store_name", storeName));
				assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				ImageModelResponseDTO actual = toModel(patchResponse.getBody(), ImageModelResponseDTO.class);
				// then
				BufferedImage actualComponent = getBufferedImageFromWeb(actual.getImageUrl());
				assertThat(actual.getImageUrl())
						.withFailMessage("Image was not uploaded to remote")
						.isNotEqualTo(prevImg.getImageUrl());
				BufferedImageAssert.assertThat(actualComponent)
						.withFailMessage("Image is same as the previous one")
						.hasDifferentDimensionsAs(prevImgComponent);
			}

			@ParameterizedTest
			@CsvSource({
					"jhosrei, f_piotr-i-pawel.png",
					"erijagoe4, f_intermarche.webp",
					"eiw0-szmfcjwe, f_biedronka.png"
			})
			@DisplayName("PUT: '/api/image?store_name=' [STORE_NOT_FOUND]")
			public void shouldReturn404WhenStoreIsNotFoundTest(String storeName, String imgFile) {
				// given
				ImageRequestDTO request = createImageRequest(getRawPathToImage("store/" + imgFile));
				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/image", request,
						Map.of("store_name", storeName));
				// then
				assertIsError(patchResponse.getBody(),
						HttpStatus.NOT_FOUND,
						"Unable to find store of '%s' name".formatted(storeName),
						"/api/image");
			}
		}
	}

	@Nested
	@TestClassOrder(ClassOrderer.Random.class)
	class StoreAPI {

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
			@ValueSource(longs = { 1, 4, 5, 8 })
			@DisplayName("GET: '/api/store/{store_id}/image'")
			public void whenGettingStoreImage_thenReturnOKTest(Long storeId) {
				StoreImage img = getStoreImage(storeId.longValue(), stores);

				// when
				var getResponse = getRequest("/api/store/" + storeId + "/image");
				String actualJson = getResponse.getBody();
				ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected = createImageResponse(img, img.getStoreName());
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
						city, street, createImageResponse(expectedFilename, name, actual.getImage(),
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
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PutRequests {

			private List<Store> stores;

			private List<StoreImage> storeImages;

			private JdbcTemplate jdbcTemplate;

			@Autowired
			public PutRequests(List<Store> stores, List<StoreImage> storeImages, DataSource dataSource) {
				this.stores = stores;
				this.storeImages = storeImages;
				this.jdbcTemplate = new JdbcTemplate(dataSource);
			}

			@ParameterizedTest
			@CsvSource({
					"5, Primo, Olsztyn, ul. Okulickiego 15",
					"4, Dwojka, Gdansk, al. Hallera 121"
			})
			@DisplayName("PUT: '/api/store' single store with image replacement with new brand removes image")
			@DirtiesContext
			public void replacingSingleEntityOfStoreWithImageShouldDeleteImageTest
					(Long storeId, String name, String city, String street) throws InterruptedException {
				Store store = getStore(storeId, stores);
				String initialUrl = store.getImage().get().getImageUrl();
				// given
				StoreRequestDTO request = createStoreRequest(name, city, street);
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				var getResponse = getRequest("/api/image", Map.of("store_name", store.getName()));
				Integer count = jdbcTemplate.queryForObject
						("SELECT count(*) FROM store_image WHERE store_name = ?",
								Integer.class, name);
				// then
				assertIsError(getResponse.getBody(),
						HttpStatus.NOT_FOUND,
						"Unable to find store of '%s' name".formatted(store.getName()),
						"/api/image");
				// asserting that there's no entity of ${name} in the
				// store_image table (because it should've been deleted)
				assertThat(count)
						.withFailMessage("'%s' was found in 'store_image' table"
								.formatted(store.getName()))
						.isEqualTo(0);
				// asserting that ImageIO.read throws IOException
				// which would mean the image is not found remotely
				Thread.sleep(900);
				assertThat(getBufferedImageFromWeb(initialUrl))
						.withFailMessage("Image was supposed to be deleted from remote")
						.isNull();
			}

			@ParameterizedTest
			@CsvSource({
					"7, Carrefour, Jedwabno, ul. Dluzna 15",
					"2, Lubi, Olsztyn, ul. Mazurska 52"
			})
			@DisplayName("PUT: '/api/store' no image in dto but image is already uploaded")
			@DirtiesContext
			public void replacingEntityWithImagePreviouslyTest(Long storeId, String name, String city, String street) {
				// given
				StoreRequestDTO request = createStoreRequest(name, city, street);
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				String actualJson = putResponse.getBody();
				StoreResponseDTO actual = toModel(actualJson, StoreResponseDTO.class);
				ImageModelResponseDTO actualImage = actual.getImage();
				// then
				StoreImage expected = getStoreImage(name, storeImages);
				assertThat(actualImage).isNotNull();
				assertThat(actualImage.getRemoteId()).isEqualTo(expected.getRemoteId());
				assertThat(actualImage.getImageUrl()).isEqualTo(expected.getImageUrl());
			}

			@ParameterizedTest
			@CsvSource({
					"1, Carrefour, Szczecin, ul. Rolna 22, f_carrefour.jpg",
					"6, Lubi, Olsztyn, ul. Wyzynna 9, f_lubi.jpg"
			})
			@DisplayName("PUT: '/api/store' include image to overwrite")
			@DirtiesContext
			public void replacingWithImageOverridesIfPreviousExistedTest
					(Long storeId, String name, String city, String street, String imageFile) {
				StoreImage prevImg = getStoreImage(name, storeImages);
				var prevImgDTO = createImageResponse(prevImg);
				BufferedImage notExpected = getBufferedImageFromWeb(prevImg.getImageUrl());
				// given
				StoreRequestDTO request = createStoreRequest
						(name, city, street, getRawPathToImage("store/" + imageFile));
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				StoreResponseDTO actual = toModel(putResponse.getBody(), StoreResponseDTO.class);
				ImageModelResponseDTO actualImage = actual.getImage();
				BufferedImage actualBuffImg = getBufferedImageFromWeb(actualImage.getImageUrl());
				// then
				assertThat(actualImage).isNotEqualTo(prevImgDTO);
				assertThat(actualBuffImg).isNotEqualTo(notExpected);
			}

			@ParameterizedTest
			@CsvSource({
					"2, Intermarche, Starogard, ul. Gdanska 5, f_intermarche.webp",
					"9, Lewiatan, Mragowo, ul. Miejska 27, f_lewiatan.png",
					"3, Piotr i Pawel, Kielce, ul. Liroja 33, f_piotr-i-pawel.png"
			})
			@DisplayName("PUT: '/api/store' include image to add")
			@DirtiesContext
			public void replacingWithImageCreatesNewIfPreviousNotExistedTest
					(Long storeId, String name, String city, String street, String imageFile) {
				// given
				String pathToImg = getRawPathToImage("store/" + imageFile);
				StoreRequestDTO request = createStoreRequest
						(name, city, street, pathToImg);
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				StoreResponseDTO actual = toModel(putResponse.getBody(), StoreResponseDTO.class);
				ImageModelResponseDTO actualImage = actual.getImage();
				assertThat(actualImage)
						.withFailMessage("Image was not found in the response")
						.isNotNull();
				BufferedImage actualBuffImg = getBufferedImageFromWeb(actualImage.getImageUrl());
				// then
				BufferedImage expected = getBufferedImageFromLocal(pathToImg);
				assertThat(expected)
						.withFailMessage("Image was not sent to remote")
						.isNotNull();
				BufferedImageAssert.assertThat(actualBuffImg).hasSameDimensionsAs(expected);
			}

			@ParameterizedTest
			@CsvSource({
					"7, Zabka, Ilawa, ul. Dworcowa 3, f_zabka.jpg",
					"4, Lubi, Warszawa, ul. Nowaka 5, f_lubi.jpg",
					"3, Carrefour, Olsztyn, ul. Borkowskiego 3, f_carrefour.jpg"
			})
			@DisplayName("PUT: '/api/store' [STORE_EXISTS] with image new/differing")
			public void shouldReturn409OnSameEnitityDifferentImageTest
					(Long storeId, String name, String city, String street, String imageFile) {
				// given
				String pathToImg = getRawPathToImage("store/" + imageFile);
				StoreRequestDTO request = createStoreRequest
						(name, city, street, pathToImg);
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);

				// then
				assertIsError(putResponse.getBody(),
						HttpStatus.CONFLICT,
						"Store already exists",
						"/api/store/" + storeId);
			}

			@ParameterizedTest
			@CsvSource({
					"9, Zabka, Ilawa, ul. Dworcowa 3, f_zabka.jpg",
					"5, Lubi, Warszawa, ul. Nowaka 5, f_lubi.jpg",
					"8, Carrefour, Olsztyn, ul. Borkowskiego 3, f_carrefour.jpg"
			})
			@DisplayName("PUT: '/api/store' [OBJECTS_EQUAL] with image new/differing")
			public void shouldDoNothingWhenReplacingWithSameEntityDifferentImageTest
					(Long storeId, String name, String city, String street, String imageFile) {
				// given
				String pathToImg = getRawPathToImage("store/" + imageFile);
				StoreRequestDTO request = createStoreRequest
						(name, city, street, pathToImg);
				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);

				// then
				assertIsError(putResponse.getBody(),
						HttpStatus.OK,
						"Objects are the same: nothing to update",
						"/api/store/" + storeId);
			}
		}
	}
}