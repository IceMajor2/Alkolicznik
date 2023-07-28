package com.demo.alkolicznik.api;

import java.util.List;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.image.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerResponse;
import static com.demo.alkolicznik.utils.JsonUtils.createBeerUpdateRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createImageResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getRawPathToImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.putRequestAuth;
import static com.demo.alkolicznik.utils.requests.MockRequests.mockPutRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@AutoConfigureMockMvc
@ActiveProfiles({ "main", "image" })
public class ImageModelTests {

	public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/icemajor/tr:n-get_beer/test/beer/";

	@Autowired
	private List<Beer> beers;

	public static MockMvc mockMvc;

	@Autowired
	public void setMockMvc(MockMvc mockMvc) {
		ImageModelTests.mockMvc = mockMvc;
	}

	@Nested
	class GetRequests {

		@Test
		@DisplayName("GET: '/api/beer/{beer_id}/image'")
		public void whenGettingBeerImage_thenReturnOKTest() {
			ImageModelResponseDTO expected = createImageResponse(getBeer(5L, beers).getImage().get());
			String expectedJson = toJsonString(expected);

			var response = getRequest("/api/beer/5/image");

			String actualJson = response.getBody();
			ImageModelResponseDTO actual = toModel(actualJson, ImageModelResponseDTO.class);

			assertThat(actualJson).isEqualTo(expectedJson);
			assertThat(actual).isEqualTo(expected);
		}
	}

	@Nested
	class PostRequests {

		@Test
		@DisplayName("POST: '/api/beer'")
		@DirtiesContext
		@WithUserDetails("admin")
		public void whenAddingBeerWithImage_thenReturnOKTest() {
			// given
			String filename = "kasztelan-niepasteryzowane-0.5.png";
			BeerRequestDTO request = createBeerRequest("Kasztelan", "Niepasteryzowane", null, getRawPathToImage(filename));

			// when
			var postResponse = postRequestAuth("admin", "admin", "/api/beer", request);
			assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = postResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(beers.size() + 1, "Kasztelan", "Niepasteryzowane", 0.5d,
					createImageResponse(filename, actual.getImage()));
			String expectedJson = toJsonString(expected);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("POST: '/api/beer' [INVALID_PROPORTIONS]")
		public void givenInvalidImage_whenAddingBeerImage_thenReturn400Test() {
			var postResponse = postRequestAuth("admin", "admin", "/api/beer",
					createBeerRequest("Heineken", null, 0.33,
							getRawPathToImage("heineken-0.33_proportions.webp")));

			String jsonResponse = postResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Image proportions are invalid",
					"/api/beer");
		}

		@Test
		@DisplayName("POST: '/api/beer' [FILE_NOT_FOUND]")
		public void givenInvalidPath_whenAddingBeerImage_thenReturn404Test() {
			String imgPath = getRawPathToImage("lomza-0.5.png");
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
	class PutRequests {

		@Test
		@DisplayName("PUT: '/api/beer/{beer_id}'")
		@DirtiesContext
		@WithUserDetails("admin")
		public void givenBeerWithNoImage_whenUpdatingBeerImage_thenReturnOKTest() {
			// given
			String filename = "perla-chmielowa-pils-0.5.webp";

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/beer/1",
					createBeerUpdateRequest(null, null, null, getRawPathToImage(filename)));
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(1L, "Perla", "Chmielowa Pils", 0.5,
					createImageResponse(filename, actual.getImage()));
			String expectedJson = toJsonString(expected);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/beer/{beer_id}' contained img previously")
		@DirtiesContext
		@WithUserDetails("admin")
		public void givenBeerWithImage_whenUpdatingBeerImage_thenReturnOKTest() {
			// given
			String filename = "perla-chmielowa-pils_2.webp";

			// when
			var putResponse = putRequestAuth("admin", "admin", "/api/beer/1",
					createBeerUpdateRequest(null, null, null, getRawPathToImage(filename)));
			assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
			String actualJson = putResponse.getBody();
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			// then
			BeerResponseDTO expected = createBeerResponse(1L, "Perla", "Chmielowa Pils", 0.5,
					createImageResponse(filename, actual.getImage()));
			String expectedJson = toJsonString(expected);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/beer/{beer_id}' assert changing brand deletes image")
		@DirtiesContext
		@WithUserDetails("admin")
		public void whenUpdatingBeerBrand_thenImageShouldBeDeletedTest() {
			var expected = createBeerResponse(6, "Browar Polczyn", "Zdrojowe", 0.5d, null);
			String expectedJson = toJsonString(expected);

			String actualJson = assertMockRequest(
					mockPutRequest("/api/beer/6",
							createBeerUpdateRequest("Browar Polczyn", "Zdrojowe", null, null)),
					HttpStatus.OK,
					expectedJson
			);
			BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

			assertThat(actual).isEqualTo(expected);
			assertThat(actualJson).isEqualTo(expectedJson);
		}

		@Test
		@DisplayName("PUT: '/api/beer/{beer_id}' [FILE_NOT_FOUND]")
		public void givenNoImage_whenUpdatingBeerImage_thenReturn404Test() {
			String path = getRawPathToImage("karpackie-0.5.jpg");
			var putResponse = putRequestAuth("admin", "admin", "/api/beer/5",
					createBeerUpdateRequest("Karpackie", null, 0.5, path));

			String jsonResponse = putResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.NOT_FOUND,
					"File was not found (Path: '%s')".formatted(path),
					"/api/beer/5");
		}

		@Test
		@DisplayName("PUT: '/api/beer/{beer_id}' [PROPORTIONS_INVALID]")
		public void givenInvalidProportions_whenUpdatingBeerImage_thenReturn400Test() {
			String path = getRawPathToImage("heineken-0.33_proportions.webp");
			var putResponse = putRequestAuth("admin", "admin", "/api/beer/2",
					createBeerUpdateRequest("Heineken", null, 0.33, path));

			String jsonResponse = putResponse.getBody();

			assertIsError(jsonResponse,
					HttpStatus.BAD_REQUEST,
					"Image proportions are invalid",
					"/api/beer/2");
		}
	}

	@Nested
	class DeleteRequests {

		@Test
		@DisplayName("DELETE: '/api/beer/{beer_id}/image'")
		public void whenDeletingBeerImage_thenReturnOKTest() {

		}
	}

//	@Test
//	@DisplayName("PATCH: '/api/beer/{beer_id}' changing image does not remove prices")
//	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
//	public void changingImageDoesNotRemovePricesTest() {
//		// given
//		String filename = "perla-chmielowa-pils-0.5.webp";
//		BeerUpdateDTO request = createBeerUpdateRequest(null, null, null, getRawPathToImage(filename));
//		// when
//		var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/1", request);
//		assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//		String actualJson = patchResponse.getBody();
//		BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);
//
//		// then
//		BeerResponseDTO expected = createBeerResponse(
//				1L, "Perla", "Chmielowa Pils", 0.5d,
//				createImageResponse(filename, actual.getImage().getExternalId())
//		);
//		String expectedJson = toJsonString(expected);
//		assertThat(actual).isEqualTo(expected);
//		assertThat(actualJson).isEqualTo(expectedJson);
//
//		// when
//		var getResponse = getRequest("/api/beer/1/beer-price");
//		List<BeerPriceResponseDTO> actualList = toModelList(getResponse.getBody(), BeerPriceResponseDTO.class);
//		// then
//		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//		List<BeerPriceResponseDTO> expectedPrices = getBeer(1L, beers).getPrices().stream().map(price -> {
//			BeerPriceResponseDTO responseDTO = new BeerPriceResponseDTO(price);
//			responseDTO.getBeer().setImage(
//					createImageResponse(filename, actual.getImage().getExternalId())
//			);
//			return responseDTO;
//		}).toList();
//		assertThat(actualList).hasSameElementsAs(expectedPrices);
//	}

	// TODO: Move image-specific tests from other test classes here
	// TODO: beer delete (both by param and object) must delete, if present,
	//		 the previously associated image with it
}
