package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.delete.BeerDeleteDTO;
import com.demo.alkolicznik.dto.put.BeerUpdateDTO;
import com.demo.alkolicznik.dto.requests.BeerRequestDTO;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.TestUtils.getImage;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.*;
import static com.demo.alkolicznik.utils.requests.MockRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@AutoConfigureMockMvc
public class BeerTests {

    @Autowired
    private List<Beer> beers;

    @Autowired
    private List<Store> stores;

    public static MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        BeerTests.mockMvc = mockMvc;
    }

    @Nested
    class GetRequests {

        @Test
        @DisplayName("GET: '/api/beer/{beer_id}")
        public void getBeerTest() {
            var getResponse = getRequest("/api/beer/1");
            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            String actualJson = getResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(getBeer(1L, beers));
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("GET: '/api/beer/{beer_id} [BEER_NOT_FOUND]")
        public void getBeerNotExistingStatusCheckTest() {
            var getResponse = getRequest("/api/beer/9999");

            String jsonResponse = getResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '9999' id",
                    "/api/beer/9999");
        }

        @Test
        @DisplayName("GET: '/api/beer'")
        @WithUserDetails("admin")
        public void getBeerAllArrayAuthorizedTest() {
            List<BeerResponseDTO> expected = beers.stream()
                    .map(BeerResponseDTO::new)
                    .toList();
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockGetRequest("/api/beer"),
                    HttpStatus.OK,
                    expectedJson);
            List<BeerResponseDTO> actual = toModelList(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class PutRequests {

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' volume update")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerVolumeTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0.5);

            BeerResponseDTO expected = createBeerResponse(
                    3L, "Tyskie", "Gronie", 0.5, getImage(3L, beers));
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(
                    mockPutRequest("/api/beer/3", request),
                    HttpStatus.OK,
                    expectedJson
            );
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/3");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' brand update")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerBrandTest() {
            BeerUpdateDTO request = createBeerUpdateRequest("Ksiazece", null, null);

            BeerResponseDTO expected = createBeerResponse(3L, "Ksiazece", "Gronie", 0.65, getImage(3L, beers));
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(
                    mockPutRequest("/api/beer/3", request),
                    HttpStatus.OK,
                    expectedJson
            );
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/3");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' type update")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerTypeTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "IPA", null);

            BeerResponseDTO expected = createBeerResponse(2L, "Ksiazece", "IPA", 0.5);
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(
                    mockPutRequest("/api/beer/2", request),
                    HttpStatus.OK,
                    expectedJson
            );
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/2");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [NO_PROPERTY_SPECIFIED]")
        public void updateBeerEmptyRequestTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, null);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/6", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "No property to update was specified",
                    "/api/beer/6"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [VOLUME_NON_POSITIVE]")
        public void updateBeerVolumeNegativeAndZeroRequestTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, null, 0d);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer/4"
            );

            request = createBeerUpdateRequest(null, null, -5.1d);
            putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

            jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer/4"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
        public void updateBeerNotExistsRequestTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "Chmielowe", null);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/321", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '321' id",
                    "/api/beer/321"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [BRAND_BLANK]")
        public void updateBeerBrandBlankRequestTest() {
            BeerUpdateDTO request = createBeerUpdateRequest("\t \t \n\n\n", null, null);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer/5"
            );

            request = createBeerUpdateRequest("", null, null);
            putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

            jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer/5"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [PROPERTIES_SAME]")
        public void updateBeerUnchangedTest() {
            BeerUpdateDTO request = createBeerUpdateRequest("Komes", "Porter Malinowy", 0.33);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/5", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/beer/5"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' remove type")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerSetTypeToNullTest() {
            BeerUpdateDTO request = createBeerUpdateRequest(null, "", null);

            BeerResponseDTO expected = createBeerResponse(6L, "Miloslaw", null, 0.5, getImage(6L, beers));
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(
                    mockPutRequest("/api/beer/6", request),
                    HttpStatus.OK,
                    expectedJson
            );
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/6");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' [PROPERTIES_SAME] (2)")
        public void updateBeerUnchangedTwoTest() {
            BeerUpdateDTO request = createBeerUpdateRequest("Zubr", null, 0.5);
            var putResponse = putRequestAuth("admin", "admin", "/api/beer/4", request);

            String jsonResponse = putResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.OK,
                    "Objects are the same: nothing to update",
                    "/api/beer/4"
            );
        }

        @Test
        @DisplayName("PUT: '/api/beer/{beer_id}' add type")
        @DirtiesContext
        @WithUserDetails("admin")
        public void updateBeerWithTypeNullTest() {
            BeerUpdateDTO request = createBeerUpdateRequest("Zubr", "Ciemnozloty", 0.5);

            BeerResponseDTO expected = createBeerResponse(4L, "Zubr", "Ciemnozloty", 0.5, getImage(4L, beers));
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(
                    mockPutRequest("/api/beer/4", request),
                    HttpStatus.OK,
                    expectedJson
            );
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);

            var getResponse = getRequest("/api/beer/4");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }

    @Nested
    class PostRequests {

        @Test
        @DisplayName("POST: '/api/beer'")
        @DirtiesContext
        public void createBeerTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Lech", null, null));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Lech", null, 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and volume only")
        @DirtiesContext
        public void createBeerWithCustomVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Karmi", null, 0.6));
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Karmi", null, 0.6);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand and type only")
        @DirtiesContext
        public void createBeerWithTypePresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Ksiazece", "Wisnia", null)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Ksiazece", "Wisnia", 0.5);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' everything specified")
        @DirtiesContext
        public void createBeerWithCustomVolumeAndTypePresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Zywiec", "Jasne", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Zywiec", "Jasne", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' brand & type existing, volume unique")
        @DirtiesContext
        public void createBeerAlreadyPresentButWithDifferentVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", 0.33)
            );
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            String actualJson = postResponse.getBody();
            BeerResponseDTO actual = toModel(actualJson, BeerResponseDTO.class);

            BeerResponseDTO expected = createBeerResponse(7L, "Perla", "Chmielowa Pils", 0.33);
            String expectedJson = toJsonString(expected);
            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);

            // Fetch the newly-created beer.
            var getResponse = getRequest("/api/beer/7");

            actualJson = getResponse.getBody();
            actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }

        @Test
        @DisplayName("POST: '/api/beer' [TYPE_BLANK]")
        // @DirtiesContext
        public void createBeerWithPresentButBlankTypeStatusCheckTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Heineken", " ", null));

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [VOLUME_NON_POSITIVE]")
        // @DirtiesContext
        public void createBeerWithNegativeVolumeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Pilsner Urquell", null, -0.5)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Lomza", null, 0d)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Volume must be a positive number",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_NULL]")
        // @DirtiesContext
        public void createBeerWithNoBrandTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(null, "Jasne Okocimskie", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_BLANK]")
        // @DirtiesContext
        public void createBeerWithBlankBrandTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(" \t \t  \t\t ", "Cerny", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("", "Cerny", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [TYPE_BLANK]")
        // @DirtiesContext
        public void createBeerWithBlankType() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Miloslaw", "  \t\t ", 0.6)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Miloslaw", "", null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Type was not specified",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BEER_EXISTS]")
        // @DirtiesContext
        public void createBeerAlreadyPresentTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Perla", "Chmielowa Pils", null)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");

            postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest("Zubr", null, null)
            );

            jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.CONFLICT,
                    "Beer already exists",
                    "/api/beer");
        }

        @Test
        @DisplayName("POST: '/api/beer' [BRAND_NULL; TYPE_BLANK; VOLUME_NEGATIVE]")
        // @DirtiesContext
        public void createBeerBrandNullTypeBlankVolumeNegativeTest() {
            var postResponse = postRequestAuth("admin", "admin",
                    "/api/beer",
                    createBeerRequest(null, " \t", -15.9)
            );

            String jsonResponse = postResponse.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.BAD_REQUEST,
                    "Brand was not specified; Type was not specified; Volume must be a positive number",
                    "/api/beer");
        }
    }

    @Nested
    class DeleteRequests {

        @Test
        @DisplayName("DELETE: '/api/beer/{beer_id}'")
        @DirtiesContext
        @WithUserDetails("admin")
        public void deleteBeerByIdTest() {
            BeerDeleteDTO expected = createBeerDeleteResponse(
                    getBeer(6L, beers),
                    "Beer was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);

            String actualJson = assertMockRequest(mockDeleteRequest("/api/beer/6"),
                    HttpStatus.OK,
                    expectedJson);
            assertThat(actualJson).isEqualTo(expectedJson);

            var getRequest = getRequest("/api/beer/6");

            String jsonResponse = getRequest.getBody();

            assertIsError(jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '6' id",
                    "/api/beer/6");
        }

        @Test
        @DisplayName("DELETE: '/api/beer'")
        @DirtiesContext
        @WithUserDetails("admin")
        public void deleteBeerByPropertiesTest() {
            BeerDeleteDTO expected = createBeerDeleteResponse(
                    getBeer(3L, beers),
                    "Beer was deleted successfully!"
            );
            String expectedJson = toJsonString(expected);

            BeerRequestDTO requestDTO = createBeerRequest(getBeer(3L, beers));
            String actualJson = assertMockRequest(
                    mockDeleteRequest(requestDTO, "/api/beer"),
                    HttpStatus.OK,
                    expectedJson
            );

            BeerDeleteDTO actual = toModel(actualJson, BeerDeleteDTO.class);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("DELETE: '/api/beer/{beer_id}' [BEER_NOT_FOUND]")
        public void deleteBeerNotExistsTest() {
            var deleteResponse = deleteRequestAuth("admin", "admin",
                    "/api/beer/0");

            String jsonResponse = deleteResponse.getBody();

            assertIsError(
                    jsonResponse,
                    HttpStatus.NOT_FOUND,
                    "Unable to find beer of '0' id",
                    "/api/beer/0"
            );
        }
    }
}

