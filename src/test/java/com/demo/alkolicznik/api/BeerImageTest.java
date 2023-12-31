package com.demo.alkolicznik.api;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerResponseDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.image.BeerImageResponseDTO;
import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.image.BeerImage;
import com.demo.alkolicznik.utils.matchers.BufferedImageAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.demo.alkolicznik.utils.matchers.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.FileUtils.*;
import static com.demo.alkolicznik.utils.FindingUtils.getBeer;
import static com.demo.alkolicznik.utils.FindingUtils.getBeerImage;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.*;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.patchRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "image", "no-security", "no-vaadin"})
@TestClassOrder(ClassOrderer.Random.class)
public class BeerImageTest {

    @Autowired
    private int pollIntervals;

    @Autowired
    private int pollIntervalsUntil;

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
            @ValueSource(longs = {3, 4, 5, 6})
            @DisplayName("GET: '/api/beer/{beer_id}/image'")
            public void whenGettingBeerImage_thenReturnOKTest(Long beerId) {
                BeerImage img = getBeerImage(beerId.longValue(), beerImages);

                // when
                var getResponse = getRequest("/api/beer/" + beerId + "/image");
                String actualJson = getResponse.getBody();
                BeerImageResponseDTO actual = toModel(actualJson, BeerImageResponseDTO.class);

                // then
                BeerImageResponseDTO expected = createImageResponse(img);
                String expectedJson = toJsonString(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
                assertThat(actual).isEqualTo(expected);
            }

            @ParameterizedTest
            @ValueSource(longs = {-1238, 0, 19824})
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
            @ValueSource(longs = {1, 2, 7})
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
                List<BeerImageResponseDTO> actual = toModelList(getResponse.getBody(),
                        BeerImageResponseDTO.class);
                // then
                List<BeerImageResponseDTO> expected = beerImages.stream()
                        .map(BeerImageResponseDTO::new)
                        .toList();
                assertThat(actual).containsExactlyElementsOf(expected);
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.Random.class)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        class PostRequests {

            @ParameterizedTest
            @CsvSource({
                    "1, perla-chmielowa-pils-0.5.webp",
                    "9, perla-chmielowa-pils_2.webp",
                    "8, namyslow.png"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image'")
            @DirtiesContext
            public void addBeerImageTest(Long beerId, String imageFile) {
                // given
                BufferedImage toUpload = getBufferedImageFromLocal
                        (getRawPathToImage("beer_not_added/" + imageFile));
                ImageRequestDTO request = createImageRequest
                        (getRawPathToImage("beer_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                BeerImageResponseDTO actual = toModel(postResponse.getBody(), BeerImageResponseDTO.class);
                String urlNoTransformation = removeTransformationFromURL
                        (actual.getUrl());
                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(urlNoTransformation) != null);
                BufferedImage actualImg = getBufferedImageFromWeb(urlNoTransformation);
                // then
                assertThat(actualImg)
                        .withFailMessage("Image was not uploaded to remote")
                        .isNotNull();
                BufferedImageAssert.assertThat(actualImg)
                        .withFailMessage("Remote image do not match the one uploaded")
                        .hasSameDimensionsAs(toUpload);
            }

            @ParameterizedTest
            @CsvSource({
                    "3, miloslaw-pilzner.png",
                    "4, kasztelan-niepasteryzowane-0.5.png"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image' [IMAGE_ALREADY_EXISTS]")
            public void addBeerImage_imageAlreadyExistsTest(Long beerId, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest
                        (getRawPathToImage("beer_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.CONFLICT,
                        "Beer already has an image",
                        "/api/beer/" + beerId + "/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "2, not_image_1.rar",
                    "7, not_image_2.rtf"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image' [FILE_NOT_IMAGE]")
            public void addBeerImage_givenNotImageTest(Long beerId, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest
                        (getRawPathToImage(imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Attached file is not an image",
                        "/api/beer/" + beerId + "/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "8, prop_hopfe.webp",
                    "7, prop_guinness.jpg",
                    "1, prop_heineken.webp"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image' [PROPORTIONS_INVALID]")
            public void addBeerImage_givenInvalidProportionsTest(Long beerId, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest
                        (getRawPathToImage("beer_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.BAD_REQUEST,
                        "Image proportions are invalid",
                        "/api/beer/" + beerId + "/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "7, jsdeepor.webp",
                    "1, kjs8.jpg"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image' [FILE_NOT_FOUND]")
            public void addBeerImage_givenFileNotFoundTest(Long beerId, String imageFile) {
                // given
                String pathToImg = getRawPathToImage(imageFile);
                ImageRequestDTO request = createImageRequest(pathToImg);
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "File was not found (Path: '%s')".formatted(pathToImg),
                        "/api/beer/" + beerId + "/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "0, miloslaw-pilzner.png",
                    "-3259023, zywiec-jasne-0.33.jpg",
                    "823459, namyslow.png"
            })
            @DisplayName("POST: '/api/beer/{beer_id}/image' [BEER_NOT_FOUND]")
            public void addBeerImage_beerNotFoundTest(Long beerId, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest
                        (getRawPathToImage("beer_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/beer/" + beerId + "/image", request);
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find beer of '%d' id".formatted(beerId),
                        "/api/beer/" + beerId + "/image");
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
            @ValueSource(longs = {3, 4, 5})
            @DisplayName("DELETE: '/api/beer/{beer_id}/image'")
            @DirtiesContext
            public void whenDeletingBeerImage_thenReturnOKTest(Long beerId) {
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/beer/" + beerId + "/image");
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                String actualJson = deleteResponse.getBody();

                // then
                String expectedJson = "{\"message\":\"Beer image was deleted successfully!\"}";
                assertThat(actualJson).isEqualTo(expectedJson);
                var getResponse = getRequest("/api/beer/" + beerId + "/image");
                assertIsError(getResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this beer",
                        "/api/beer/" + beerId + "/image");
            }

            @ParameterizedTest
            @ValueSource(longs = {1, 2, 7, 8})
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

            private List<BeerImage> beerImages;

            @Autowired
            public PutRequests(List<BeerImage> beerImages) {
                this.beerImages = beerImages;
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "4, Lech, Premium, null",
                    "3, Ksiazece, IPA, 0.33",
                    "6, Namyslow, null, null"
            }, nullValues = "null")
            @DisplayName("PUT: '/api/beer/{beer_id}' successful replacement removes previous image")
            @DirtiesContext
            public void replacingBeerRemovesPreviousImageTest(Long beerId, String brand,
                                                              String type, Double volume) throws InterruptedException {
                // given
                BeerRequestDTO request = createBeerRequest(brand, type, volume);
                String previous_urlToDelete = getBeerImage(beerId, beerImages).getImageUrl();
                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequest("/api/beer/" + beerId + "/image");
                // then
                assertIsError(getResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this beer",
                        "/api/beer/" + beerId + "/image");

                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(previous_urlToDelete) == null);
                BufferedImage expectedRemoved = getBufferedImageFromWeb(previous_urlToDelete);
                assertThat(expectedRemoved)
                        .withFailMessage("Image was expected to be deleted from remote")
                        .isNull();
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
                // given
                Beer beer = getBeer(beerId.longValue(), beers);
                BeerUpdateDTO request = createBeerUpdateRequest(null, null, volume);

                // when
                var patchResponse = patchRequestAuth("admin", "admin", "/api/beer/" + beerId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                var getResponse = getRequest("/api/beer/" + beerId + "/image");
                BeerImageResponseDTO actual = toModel(getResponse.getBody(), BeerImageResponseDTO.class);

                // then
                BeerImageResponseDTO expected = createImageResponse(beer.getImage().get());
                assertThat(actual).isEqualTo(expected);
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "3, Tyskie, Gronie, 0.33",
                    "4, Zubr, null, 0.6"
            }, nullValues = "null")
            @DisplayName("PATCH: '/api/beer/{beer_id}' volume update does not remove image 2")
            @DirtiesContext
            public void shouldNotRemoveImageOnVolumeUpdateTest(Long beerId, String brand, String type, Double volume) {
                // given
                Beer beer = getBeer(beerId.longValue(), beers);
                BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

                // when
                var patchResponse = patchRequest("/api/beer/" + beerId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

                var getResponse = getRequest("/api/beer/" + beerId + "/image");
                BeerImageResponseDTO actual = toModel(getResponse.getBody(), BeerImageResponseDTO.class);

                // then
                BeerImageResponseDTO expected = createImageResponse(beer.getImage().get());
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
            @ValueSource(longs = {6, 4, 5})
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
                BeerRequestDTO request = createBeerRequest(brand, type, volume);

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
