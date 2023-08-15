package com.demo.alkolicznik.api;

import java.util.List;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.image.ImageDeleteDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
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
import static com.demo.alkolicznik.utils.JsonUtils.createBeerDeleteRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createImageDeleteResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.JsonUtils.toModelList;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getBeerImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.deleteRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.getRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.patchRequestAuth;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "enable.image.database=true")
@Import(DisabledVaadinContext.class)
@ActiveProfiles({ "main", "image" })
@TestClassOrder(ClassOrderer.Random.class)
public class BeerImageTest {

	public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/alkolicznik/tr:n-get_beer/test/beer/";

	@Nested
	@TestClassOrder(ClassOrderer.Random.class)
	class ImageAPI {

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class GetRequests {

			private List<BeerImage> beerImages;

			@Autowired
			public GetRequests(List<BeerImage> beerImages) {
				this.beerImages = beerImages;
			}

			@ParameterizedTest
			@ValueSource(longs = { 3, 4, 5, 6 })
			@DisplayName("GET: '/api/beer/{beer_id}/image'")
			public void whenGettingBeerImage_thenReturnOKTest(Long beerId) {
				BeerImage img = getBeerImage(beerId.longValue(), beerImages);

				// when
				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				String actualJson = getResponse.getBody();
				ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

				// then
				ImageModelResponseDTO expected = createImageResponse(img);
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

			@Test
			@DisplayName("GET: '/api/beer/image'")
			public void shouldReturnAllBeerImagesTest() {
				// when
				var getResponse = getRequestAuth("admin", "admin", "/api/beer/image");
				assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				List<ImageModelResponseDTO> actual = toModelList(getResponse.getBody(),
						ImageModelResponseDTO.class);
				// then
				List<ImageModelResponseDTO> expected = beerImages.stream()
						.map(ImageModelResponseDTO::new)
						.toList();
				assertThat(actual).containsExactlyElementsOf(expected);
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class DeleteRequests {

			private List<Beer> beers;

			@Autowired
			public DeleteRequests(List<Beer> beers) {
				this.beers = beers;
			}

			@ParameterizedTest
			@ValueSource(longs = { 3, 4, 5 })
			@DisplayName("DELETE: '/api/beer/{beer_id}/image'")
			@DirtiesContext
			public void whenDeletingBeerImage_thenReturnOKTest(Long beerId) {
				// given
				Beer beer = getBeer(beerId.longValue(), beers);
				// when
				var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId + "/image");
				assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				String actualJson = deleteResponse.getBody();
				ImageDeleteDTO actual = toModel(actualJson, ImageDeleteDTO.class);

				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				assertIsError(getResponse.getBody(),
						HttpStatus.NOT_FOUND,
						"Unable to find image for this beer",
						"/api/beer/" + beerId + "/image");

				// then
				beer.setImage(null);
				ImageDeleteDTO expected = createImageDeleteResponse(beer,
						"Image was deleted successfully!");
				String expectedJson = toJsonString(expected);
				assertThat(actual).isEqualTo(expected);
				assertThat(actualJson).isEqualTo(expectedJson);
			}

			@ParameterizedTest
			@ValueSource(longs = { 1, 2, 7, 8 })
			@DisplayName("DELETE: '/api/beer/{beer_id}/image' [IMAGE_NOT_FOUND]")
			public void deleteImageNotFoundTest(Long beerId) {
				var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId + "/image");
				String actualJson = deleteResponse.getBody();

				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Unable to find image for this beer",
						"/api/beer/" + beerId + "/image");
			}
		}
	}

	@Nested
	@TestClassOrder(ClassOrderer.Random.class)
	class BeerAPI {

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class PutRequests {
			// TODO: successful replacement should delete previous image
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
			@CsvSource({
					"3, 0.5",
					"5, 0.6"
			})
			@DisplayName("PATCH: '/api/beer/{beer_id}' volume update does not remove image")
			@DirtiesContext
			public void updateVolumeShouldNotRemoveImageTest(Long beerId, Double volume) {
				Beer beer = getBeer(beerId.longValue(), beers);
				// given
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
		}

		@Nested
		@TestMethodOrder(MethodOrderer.Random.class)
		@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
		class DeleteRequests {

			private List<Beer> beers;

			@Autowired
			public DeleteRequests(List<Beer> beers) {
				this.beers = beers;
			}

			@ParameterizedTest
			@ValueSource(longs = { 6, 4, 5 })
			@DisplayName("DELETE: '/api/beer/{beer_id}' by id")
			@DirtiesContext
			public void deleteBeerByIdRemovesImageTest(Long beerId) {
				// given
				Beer beer = getBeer(beerId.longValue(), beers);
				assertThat(beer.getImage()).isNotEmpty();

				// when
				var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId);
				assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				String actualJson = getResponse.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Unable to find beer of '%d' id".formatted(beerId),
						"/api/beer/" + beerId + "/image");
			}

			@ParameterizedTest
			@CsvSource(value = {
					"3, Tyskie, Gronie, 0.65",
					"4, Zubr, null, null",
					"6, Miloslaw, Biale, null"
			}, nullValues = "null")
			@DisplayName("DELETE: '/api/beer/{beer_id}' by object")
			@DirtiesContext
			public void deleteBeerByObjectRemovesImageTest(Long beerId, String brand, String type, Double volume) {
				// given
				Beer beer = getBeer(beerId.longValue(), beers);
				assertThat(beer.getImage()).isNotEmpty();
				BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

				// when
				var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer", request);
				assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
				var getResponse = getRequest("/api/beer/" + beerId + "/image");
				String actualJson = getResponse.getBody();

				// then
				assertIsError(actualJson,
						HttpStatus.NOT_FOUND,
						"Unable to find beer of '%d' id".formatted(beerId),
						"/api/beer/" + beerId + "/image");
			}
		}
	}
}
