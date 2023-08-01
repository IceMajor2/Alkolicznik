package com.demo.alkolicznik.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.beerprice.BeerPriceResponseDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.ImageModel;
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
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.JsonUtils.toModelList;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getRawPathToImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
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
public class ImageModelTests {

	@Nested
	@TestClassOrder(ClassOrderer.Random.class)
	public class BeerImages {

		public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/icemajor/tr:n-get_beer/test/beer/";

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class GetRequests {

			private List<Beer> beers;

			@Autowired
			public GetRequests(List<Beer> beers) {
				this.beers = beers;
			}

			@ParameterizedTest
			@ValueSource(longs = { 3, 4, 5, 6 })
			@DisplayName("GET: '/api/beer/{beer_id}/image'")
			public void whenGettingBeerImage_thenReturnOKTest(Long beerId) {
				Optional<ImageModel> img = getBeer(beerId.longValue(), beers).getImage();
				assertThat(img).isNotEmpty();

				// when
				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				String actualJson = getResponse.getBody();
				ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected = createImageResponse(img.get());
				String expectedJson = toJsonString(expected);
				assertThat(actualJson).isEqualTo(expectedJson);
				assertThat(actual).isEqualTo(expected);
			}

			@ParameterizedTest
			@ValueSource(longs = { -1238, 0, 19824 })
			@DisplayName("GET: '/api/beer/{beer_id}/image' [BEER_NOT_FOUND]")
			public void shouldReturnNotFoundOnInvalidBeerIdTest(Long beerId) {
				var getResponse = getRequest("/api/beer/" + beerId + "/image");

				String actualJson = getResponse.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Unable to find beer of '%d' id".formatted(beerId),
						"/api/beer/" + beerId + "/image");
			}

			@ParameterizedTest
			@ValueSource(longs = { 1, 2, 7 })
			@DisplayName("GET: '/api/beer/{beer_id}/image' [NO_IMAGE]")
			public void shouldReturnNotFoundOnBeerWithNoImageTest(Long beerId) {
				var getResponse = getRequest("/api/beer/" + beerId + "/image");

				String actualJson = getResponse.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Unable to find image for this beer",
						"/api/beer/" + beerId + "/image");
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PostRequests {

			private List<Beer> beers;

			@Autowired
			public PostRequests(List<Beer> beers) {
				this.beers = beers;
			}

			@ParameterizedTest
			@CsvSource(value = {
					"Perla, Chmielowa Pils, 0.6, perla-chmielowa-pils_2.webp, perla-chmielowa-pils-0.6.webp",
					"Zywiec, Jasne, 0.33, zywiec-jasne-0.33.jpg, zywiec-jasne-0.33.jpg",
					"Namyslow, null, null, namyslow.png, namyslow-0.5.png"
			}, nullValues = "null")
			@DisplayName("POST: '/api/beer'")
			@DirtiesContext
			public void whenAddingBeerWithImage_thenReturnOKTest(String brand, String type,
					Double volume, String filename, String expectedFilename) {
				// given
				BeerRequestDTO request = createBeerRequest(brand, type, volume, getRawPathToImage(filename));

				// when
				var postResponse = postRequestAuth("admin", "admin", "/api/beer", request);
				assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
				String actualJson = postResponse.getBody();
				BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

				// then
				assertThat(actual.getImage().getExternalId())
						.withFailMessage("The image's external id was null. That means it "
								+ "probably was not send to the remote server.")
						.isNotNull();
				volume = volume == null ? 0.5 : volume;
				BeerResponseDTO expected = createBeerResponse(beers.size() + 1, brand, type,
						volume, createImageResponse(expectedFilename, actual.getImage()));
				String expectedJson = toJsonString(expected);
				assertThat(actual).isEqualTo(expected);
				assertThat(actualJson).isEqualTo(expectedJson);

				// when
				var getResponse = getRequest("/api/beer/" + (beers.size() + 1) + "/image");
				actualJson = getResponse.getBody();
				ImageModelResponseDTO actual_2 = toModel(actualJson, ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected_2 = expected.getImage();
				expectedJson = toJsonString(expected_2);
				assertThat(actualJson).isEqualTo(expectedJson);
				assertThat(actual_2).isEqualTo(expected_2);
			}

			@ParameterizedTest
			@ValueSource(strings = { "prop_heineken.webp", "prop_guinness.jpg", "prop_hopfe.webp" })
			@DisplayName("POST: '/api/beer' [INVALID_PROPORTIONS]")
			public void givenInvalidImage_whenAddingBeerImage_thenReturn400Test(String filename) {
				var postResponse = postRequestAuth("admin", "admin", "/api/beer",
						createBeerRequest("Heineken", null, 0.33,
								getRawPathToImage(filename)));

				String jsonResponse = postResponse.getBody();

				assertIsError(jsonResponse,
						HttpStatus.BAD_REQUEST,
						"Image proportions are invalid",
						"/api/beer");
			}

			@Test
			@DisplayName("POST: '/api/beer' [FILE_NOT_FOUND]")
			public void givenInvalidPath_whenAddingBeerImage_thenReturn404Test() {
				String imgPath = getRawPathToImage("kljhvdfsur.png");
				var postResponse = postRequestAuth("admin", "admin", "/api/beer",
						createBeerRequest("Lomza", null, null, imgPath));

				String jsonResponse = postResponse.getBody();

				assertIsError(jsonResponse,
						HttpStatus.NOT_FOUND,
						"File was not found (Path: '%s')".formatted(imgPath),
						"/api/beer");
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PutRequests {

			@ParameterizedTest
			@CsvSource(value = {
					"2, Miloslaw, Pilzner, 0.5, miloslaw-pilzner.png, miloslaw-pilzner-0.5.png",
					"4, Zywiec, Jasne, 0.33, zywiec-jasne-0.33.jpg, zywiec-jasne-0.33.jpg",
					"6, Namyslow, null, null, namyslow.png, namyslow-0.5.png"
			}, nullValues = "null")
			@DisplayName("PUT: '/api/beer/{beer_id}'")
			@DirtiesContext
			public void replacingBeerWithBeerWithImageShouldReturnOkTest(Long replaceId, String brand,
					String type, Double volume, String filename, String expectedFilename) {
				// given
				BeerRequestDTO request =
						createBeerRequest(brand, type, volume, getRawPathToImage(filename));

				// when
				var putResponse = putRequestAuth("admin", "admin", "/api/beer/" + replaceId, request);
				assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				String actualJson = putResponse.getBody();
				BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

				// then
				assertThat(actual.getImage().getExternalId())
						.withFailMessage("The image's external id was null. That means it "
								+ "probably was not send to the remote server.")
						.isNotNull();
				volume = volume == null ? 0.5 : volume;
				BeerResponseDTO expected = createBeerResponse(replaceId, brand, type, volume,
						createImageResponse(expectedFilename, actual.getImage()));
				String expectedJson = toJsonString(expected);
				assertThat(actual).isEqualTo(expected);
				assertThat(actualJson).isEqualTo(expectedJson);

				// when
				var getResponse = getRequest("/api/beer/" + replaceId + "/image");
				actualJson = getResponse.getBody();
				ImageModelResponseDTO actual_2 = toModel(actualJson, ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected_2 = expected.getImage();
				expectedJson = toJsonString(expected_2);
				assertThat(actualJson).isEqualTo(expectedJson);
				assertThat(actual_2).isEqualTo(expected_2);
			}

			@Test
			@DisplayName("PUT: '/api/beer/{beer_id}' [FILE_NOT_FOUND]")
			public void givenNoImage_whenUpdatingBeerImage_thenReturn404Test() {
				String path = getRawPathToImage("gdfijh.webp");
				var putResponse = putRequestAuth("admin", "admin", "/api/beer/5",
						createBeerRequest("Karpackie", null, 0.5, path));

				String jsonResponse = putResponse.getBody();

				assertIsError(jsonResponse,
						HttpStatus.NOT_FOUND,
						"File was not found (Path: '%s')".formatted(path),
						"/api/beer/5");
			}

			@ParameterizedTest
			@ValueSource(strings = { "prop_heineken.webp", "prop_guinness.jpg", "prop_hopfe.webp" })
			@DisplayName("PUT: '/api/beer/{beer_id}' [INVALID_PROPORTIONS]")
			public void givenInvalidProportions_whenReplacingBeer_thenReturn400Test(String filename) {
				String path = getRawPathToImage(filename);
				var putResponse = putRequestAuth("admin", "admin", "/api/beer/2",
						createBeerRequest("Debowe", "Mocne", null, path));

				String jsonResponse = putResponse.getBody();

				assertIsError(jsonResponse,
						HttpStatus.BAD_REQUEST,
						"Image proportions are invalid",
						"/api/beer/2");
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PatchRequests {

			private List<Beer> beers;

			@Autowired
			public PatchRequests(List<Beer> beers) {
				this.beers = beers;
			}

			@ParameterizedTest
			@CsvSource({
					"7, 0.5, namyslow.png, guinness-0.5.png",
					"4, 0.5, kasztelan-niepasteryzowane-0.5.png, zubr-0.5.png",
					"5, 0.33, zywiec-jasne-0.33.jpg, komes-porter-malinowy-0.33.jpg"
			})
			@DisplayName("PATCH: '/api/beer/{beer_id}'")
			@DirtiesContext
			public void updateBeerWithImageTest(Long beerId, Double volume,
					String filename, String expectedFilename) {
				Beer beer = getBeer(beerId, beers);
				// given
				BeerUpdateDTO request = createBeerUpdateRequest
						(null, null, volume, getRawPathToImage(filename));

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
				assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				String actualJson = patchResponse.getBody();
				BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

				// then
				assertThat(actual.getImage().getExternalId())
						.withFailMessage("The image's external id was null. That means it "
								+ "probably was not send to the remote server.")
						.isNotNull();
				beer.setVolume(volume);
				BeerResponseDTO expected = createBeerResponse(beer,
						createImageResponse(expectedFilename, actual.getImage()));
				String expectedJson = toJsonString(expected);
				assertThat(actual).isEqualTo(expected);
				assertThat(actualJson).isEqualTo(expectedJson);
			}

			@ParameterizedTest
			@CsvSource(value = {
					"3, null, ''",
					"4, null, Ciemnozloty",
					"6, Ksiazece, null"
			}, nullValues = "null")
			@DisplayName("PATCH: '/api/beer/{beer_id}' brand / type update removes image")
			@DirtiesContext
			public void updateBeerBrandOrTypeShouldRemoveImageTest(Long beerId, String brand, String type) {
				// given
				BeerUpdateDTO request = createBeerUpdateRequest(brand, type, null);
				Beer toUpdate = getBeer(beerId.longValue(), beers);
				assertThat(toUpdate.getImage()).isNotEmpty();

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
				assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

				var getResponse = getRequest("/api/beer/" + beerId);
				String actualJson = getResponse.getBody();
				BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

				// then
				assertThat(actual.getImage()).isNull();
			}

			@ParameterizedTest
			@CsvSource(value = {
					"1, namyslow.png",
					"5, kasztelan-niepasteryzowane-0.5.png",
					"9, zywiec-jasne-0.33.jpg"
			})
			@DisplayName("PATCH: '/api/beer/{beer_id}' image update does not remove prices")
			@DirtiesContext
			public void updateImageShouldNotRemovePricesTest(Long beerId, String filename) {
				// given
				Beer beer = getBeer(beerId.longValue(), beers);
				var prices = beer.getPrices();
				assertThat(prices).isNotEmpty();
				BeerUpdateDTO request =
						createBeerUpdateRequest(null, null, null, getRawPathToImage(filename));

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
				assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				ImageModelResponseDTO actualImg = toModel(patchResponse.getBody(), BeerResponseDTO.class).getImage();

				var getResponse = getRequestAuth("admin", "admin", "/api/beer/" + beerId + "/beer-price");
				assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				String actualJson = getResponse.getBody();
				List<BeerPriceResponseDTO> actualPrices = toModelList(actualJson, BeerPriceResponseDTO.class);

				// then
				List<BeerPriceResponseDTO> expectedPrices = prices.stream()
						.map(price -> {
							BeerPriceResponseDTO dto = new BeerPriceResponseDTO(price);
							dto.getBeer().setImage(actualImg);
							return dto;
						})
						.collect(Collectors.toList());
				assertThat(actualPrices).hasSameElementsAs(expectedPrices);
			}

			@ParameterizedTest
			@CsvSource({
					"3, 0.5",
					"5, 0.6"
			})
			@DisplayName("PATCH: '/api/beer/{beer_id}' volume update does not remove image")
			@DirtiesContext
			public void updateVolumeShouldNotRemoveImageTest(Long beerId, Double volume) {
				// given
				Beer beer = getBeer(beerId.longValue(), beers);
				assertThat(beer.getImage()).isNotEmpty();
				BeerUpdateDTO request = createBeerUpdateRequest(null, null, volume);

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
				assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				ImageModelResponseDTO actual = toModel(getResponse.getBody(), ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected = createImageResponse(beer.getImage().get());
				assertThat(actual).isEqualTo(expected);
			}

			@Test
			@DisplayName("PATCH: '/api/beer/{beer_id}' [FILE_NOT_FOUND]")
			public void updateBeerImageFileNotFoundTest() {
				// given
				String imgPath = getRawPathToImage("kljhvdfsur.png");
				BeerUpdateDTO request = createBeerUpdateRequest(null, null, null, imgPath);

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/2", request);
				String actualJson = patchResponse.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"File was not found (Path: '%s')".formatted(imgPath),
						"/api/beer/2");
			}

			@ParameterizedTest
			@ValueSource(strings = { "prop_heineken.webp", "prop_guinness.jpg", "prop_hopfe.webp" })
			@DisplayName("PATCH: '/api/beer/{beer_id}' [INVALID_PROPORTIONS]")
			public void updateBeerImageInvalidProportionsTest(String filename) {
				// given
				BeerUpdateDTO request = createBeerUpdateRequest(null, null, null, getRawPathToImage(filename));

				// when
				var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/5", request);
				String actualJson = patchResponse.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.BAD_REQUEST,
						"Image proportions are invalid",
						"/api/beer/5");
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class DeleteRequests {

			@Test
			@DisplayName("DELETE: '/api/beer/{beer_id}/image'")
			public void whenDeletingBeerImage_thenReturnOKTest() {

			}
		}
	}
}
