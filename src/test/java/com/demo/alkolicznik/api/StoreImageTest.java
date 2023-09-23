package com.demo.alkolicznik.api;

import com.demo.alkolicznik.dto.image.ImageRequestDTO;
import com.demo.alkolicznik.dto.image.StoreImageResponseDTO;
import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreResponseDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import com.demo.alkolicznik.models.Store;
import com.demo.alkolicznik.models.image.StoreImage;
import com.demo.alkolicznik.utils.matchers.BufferedImageAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.demo.alkolicznik.utils.FileUtils.*;
import static com.demo.alkolicznik.utils.FindingUtils.getStore;
import static com.demo.alkolicznik.utils.FindingUtils.getStoreImage;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.removeTransformationFromURL;
import static com.demo.alkolicznik.utils.matchers.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.patchRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "image", "no-security", "no-vaadin"})
@TestClassOrder(ClassOrderer.Random.class)
public class StoreImageTest {

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

            private List<StoreImage> storeImages;

            @Autowired
            public GetRequests(List<StoreImage> storeImages) {
                this.storeImages = storeImages;
            }

            @ParameterizedTest
            @ValueSource(strings = {"Carrefour", "ABC", "Lubi"})
            @DisplayName("GET: '/api/store/image?name='")
            public void shouldReturnImageTest(String storeName) {
                StoreImage image = getStoreImage(storeName, storeImages);

                // when
                var getResponse = getRequest("/api/store/image", Map.of("name", storeName));
                String actualJson = getResponse.getBody();
                StoreImageResponseDTO actual = toModel(actualJson, StoreImageResponseDTO.class);

                // then
                StoreImageResponseDTO expected = createImageResponse(image);
                String expectedJson = toJsonString(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
                assertThat(actual).isEqualTo(expected);
            }

            @ParameterizedTest
            @ValueSource(strings = {"ikjgsde", "032ka", "fsdkasfgd"})
            @DisplayName("GET: '/api/store/image?name=' [STORE_NOT_FOUND]")
            public void shouldReturn404OnNameNotFoundTest(String storeName) {
                var getResponse = getRequest("/api/store/image", Map.of("name", storeName));

                String actualJson = getResponse.getBody();

                assertIsError(actualJson,
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(storeName),
                        "/api/store/image"
                );
            }

            @ParameterizedTest
            @ValueSource(strings = {"Biedronka", "Grosik", "Lidl"})
            @DisplayName("GET: '/api/store/image?name=' [NO_IMAGE]")
            public void shouldReturn404OnNoImageTest(String storeName) {
                var getResponse = getRequest("/api/store/image", Map.of("name", storeName));

                String actualJson = getResponse.getBody();

                assertIsError(actualJson,
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this store",
                        "/api/store/image");
            }

            @Test
            @DisplayName("GET: '/api/store/image'")
            public void shouldReturnAllImagesTest() {
                // when
                var getResponse = getRequestAuth("admin", "admin", "/api/store/image");
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                List<StoreImageResponseDTO> actual = toModelList(getResponse.getBody(),
                        StoreImageResponseDTO.class);
                // then
                List<StoreImageResponseDTO> expected = storeImages.stream()
                        .map(StoreImageResponseDTO::new)
                        .toList();
                assertThat(actual).containsExactlyElementsOf(expected);
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
            @DisplayName("POST: '/api/store/image?name=' [STORE_NOT_FOUND]")
            public void doNotAddImageIfStoreOfNameIsNotFoundTest(String storeName, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage("store_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth
                        ("admin", "admin", "/api/store/image", request, Map.of("name", storeName));
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(storeName),
                        "/api/store/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "ABC, f_lewiatan.png",
                    "Lubi, f_lubi.jpg",
                    "Carrefour, f_carrefour.jpg"
            })
            @DisplayName("POST: '/api/store/image?name=' [IMAGE_ALREADY_EXISTS]")
            public void shouldReturn409WhenStoreAlreadyHasImageTest(String storeName, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage("store_not_added/" + imageFile));
                // when
                var postResponse = postRequestAuth
                        ("admin", "admin", "/api/store/image", request, Map.of("name", storeName));
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.CONFLICT,
                        "Store already has an image".formatted(storeName),
                        "/api/store/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "Biedronka, f_biedronka.png",
                    "Zabka, f_zabka.jpg"
            })
            @DisplayName("POST: '/api/store/image?name=' compare uploaded file with remote")
            @DirtiesContext
            public void uploadedFileShouldMatchRemoteTest(String storeName, String imageFile) {
                // given
                String pathToNewImg = getRawPathToImage("store_not_added/" + imageFile);
                ImageRequestDTO request = createImageRequest(pathToNewImg);
                // when
                var postResponse = postRequestAuth
                        ("admin", "admin", "/api/store/image", request, Map.of("name", storeName));
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                var getResponse = getRequest("/api/store/image", Map.of("name", storeName));
                StoreImageResponseDTO actualResponse = toModel
                        (getResponse.getBody(), StoreImageResponseDTO.class);
                // then
                BufferedImage expected = getBufferedImageFromLocal(pathToNewImg);
                String urlNoTransformation = removeTransformationFromURL(actualResponse.getUrl());
                BufferedImage actual = getBufferedImageFromWeb(urlNoTransformation);
                // NOTE: ImageKit compresses images if their quality is too high,
                // thus increasing the chance of a false negative. Here, I'm comparing
                // just the dimensions of the images. It is also flawed as it
                // increases the chance of a false positive.
                BufferedImageAssert.assertThat(actual).hasSameDimensionsAs(expected);
            }

            @ParameterizedTest
            @CsvSource({
                    "Tesco, not_image_1.rar",
                    "Grosik, not_image_2.rtf"
            })
            @DisplayName("POST: '/api/store/image?name=' [FILE_NOT_IMAGE]")
            public void addBeerImage_givenNotImageTest(String storeName, String imageFile) {
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage(imageFile));
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/store/image", request, Map.of("name", storeName));
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Attached file is not an image",
                        "/api/store/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "Biedronka, iosdr9045.png",
                    "Zabka, 92nmj3js.jpg"
            })
            @DisplayName("POST: '/api/store/image?name=' [FILE_NOT_FOUND]")
            public void addStoreImage_givenFileNotFoundTest(String storeName, String imageFile) {
                // given
                String pathToImg = getRawPathToImage("store_not_added\\" + imageFile);
                ImageRequestDTO request = createImageRequest(pathToImg);
                // when
                var postResponse = postRequestAuth("admin", "admin",
                        "/api/store/image", request, Map.of("name", storeName));
                // then
                assertIsError(postResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "File was not found (Path: '%s')".formatted(pathToImg),
                        "/api/store/image");
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.Random.class)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        class PutRequests {

            private List<StoreImage> storeImages;

            @Autowired
            public PutRequests(List<StoreImage> storeImages) {
                this.storeImages = storeImages;
            }

            @ParameterizedTest
            @CsvSource({
                    "Carrefour, f_carrefour.jpg",
                    "Lubi, f_lubi.jpg"
            })
            @DisplayName("PUT: '/api/store/image?name=' successful replacement with image")
            @DirtiesContext
            public void successfulReplacementOfImageEntityTest(String storeName, String imgFile) {
                StoreImage prevImg = getStoreImage(storeName, storeImages);
                BufferedImage prevImgComponent = getBufferedImageFromWeb(prevImg.getImageUrl());
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage("store_not_added/" + imgFile));
                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/store/image", request,
                        Map.of("name", storeName));
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                StoreImageResponseDTO actual = toModel(putResponse.getBody(), StoreImageResponseDTO.class);
                // then
                BufferedImage actualComponent = getBufferedImageFromWeb(actual.getUrl());
                assertThat(actual.getUrl())
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
            @DisplayName("PUT: '/api/store/image?name=' [STORE_NOT_FOUND]")
            public void shouldReturn404WhenStoreIsNotFoundTest(String storeName, String imgFile) {
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage("store_not_added/" + imgFile));
                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/store/image", request,
                        Map.of("name", storeName));
                // then
                assertIsError(putResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(storeName),
                        "/api/store/image");
            }

            @ParameterizedTest
            @CsvSource({
                    "Tesco, f_tesco.png",
                    "Biedronka, f_biedronka.webp",
            })
            @DisplayName("PUT: '/api/store/image?name=' [IMAGE_NOT_FOUND]")
            public void shouldReturn404WhenImageIsNotFoundTest(String storeName, String imgFile) {
                // given
                ImageRequestDTO request = createImageRequest(getRawPathToImage("store_not_added/" + imgFile));
                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/store/image", request,
                        Map.of("name", storeName));
                // then
                assertIsError(putResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this store".formatted(storeName),
                        "/api/store/image");
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.Random.class)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        class DeleteRequests {

            private List<StoreImage> storeImages;

            @Autowired
            public DeleteRequests(List<StoreImage> storeImages) {
                this.storeImages = storeImages;
            }

            @ParameterizedTest
            @ValueSource(strings = {"ABC", "Carrefour", "Lubi"})
            @DisplayName("DELETE: '/api/store/image?name='")
            @DirtiesContext
            public void successfulDeletionTest(String storeName) {
                StoreImage notExpected = getStoreImage(storeName, storeImages);
                String notExpectedURL = notExpected.getImageUrl();
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/image",
                        Map.of("name", storeName));
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequest("/api/store/image", Map.of("name", storeName));
                // then
                assertIsError(getResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this store",
                        "/api/store/image");

                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(notExpectedURL) == null);
                assertThat(getBufferedImageFromWeb(notExpectedURL))
                        .withFailMessage("Image was not deleted remotely")
                        .isNull();
            }

            @ParameterizedTest
            @ValueSource(strings = {"gs1krjn", "ski3jao"})
            @DisplayName("DELETE: '/api/store/image?name=' [STORE_NOT_FOUND]")
            public void shouldReturn404OnStoreNotFoundTest(String storeName) {
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/image",
                        Map.of("name", storeName));

                assertIsError(deleteResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(storeName),
                        "/api/store/image");
            }

            @ParameterizedTest
            @ValueSource(strings = {"Tesco", "Zabka"})
            @DisplayName("DELETE: '/api/store/image?name=' [IMAGE_NOT_FOUND]")
            public void shouldReturn404OnImageNotFoundTest(String storeName) {
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/image",
                        Map.of("name", storeName));

                assertIsError(deleteResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find image for this store",
                        "/api/store/image");
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
            @ValueSource(longs = {1, 4, 5, 8})
            @DisplayName("GET: '/api/store/{store_id}/image'")
            public void whenGettingStoreImage_thenReturnOKTest(Long storeId) {
                StoreImage img = getStoreImage(storeId.longValue(), stores);

                // when
                var getResponse = getRequest("/api/store/" + storeId + "/image");
                String actualJson = getResponse.getBody();
                StoreImageResponseDTO actual = toModel(actualJson, StoreImageResponseDTO.class);

                // then
                StoreImageResponseDTO expected = createImageResponse(img);
                String expectedJson = toJsonString(expected);
                assertThat(actualJson).isEqualTo(expectedJson);
                assertThat(actual).isEqualTo(expected);
            }

            @ParameterizedTest
            @ValueSource(longs = {2, 3, 7})
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
                BufferedImage actual = getBufferedImageFromWeb(actualResponse.getImage().getUrl());
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
            @DisplayName("PUT: '/api/store' single store with image replacement and new brand removes image")
            @DirtiesContext
            public void replacingSingleEntityOfStoreWithImageShouldDeleteImageTest
                    (Long storeId, String name, String city, String street) {
                Store store = getStore(storeId, stores);
                String initialUrl = store.getImage().get().getImageUrl();
                // given
                StoreRequestDTO request = createStoreRequest(name, city, street);
                // when
                var putResponse = putRequestAuth("admin", "admin", "/api/store/" + storeId, request);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequest("/api/store/image", Map.of("name", store.getName()));
                Integer count = jdbcTemplate.queryForObject
                        ("SELECT count(*) FROM store_image WHERE store_name = ?",
                                Integer.class, name);
                // then
                assertIsError(getResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(store.getName()),
                        "/api/store/image");
                // asserting that there's no entity of ${name} in the
                // store_image table (because it should've been deleted)
                assertThat(count)
                        .withFailMessage("'%s' was found in 'store_image' table"
                                .formatted(store.getName()))
                        .isEqualTo(0);
                // asserting that ImageIO.read returns null
                // which would mean the image is not found remotely
                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(initialUrl) == null);
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
                StoreImageResponseDTO actualImage = actual.getImage();
                // then
                StoreImage expected = getStoreImage(name, storeImages);
                assertThat(actualImage).isNotNull();
                assertThat(actualImage.getRemoteId()).isEqualTo(expected.getRemoteId());
                assertThat(actualImage.getUrl()).isEqualTo(expected.getImageUrl());
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.Random.class)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        class PatchRequests {

            private List<Store> stores;
            private JdbcTemplate jdbcTemplate;

            @Autowired
            public PatchRequests(List<Store> stores, DataSource dataSource) {
                this.stores = stores;
                this.jdbcTemplate = new JdbcTemplate(dataSource);
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "5, Carrefour, null, null",
                    "4, Milek, Olsztyn, null"
            }, nullValues = "null")
            @DisplayName("PATCH: '/api/store/{store_id}' updating unique store name removes image")
            @DirtiesContext
            public void shouldRemoveImageOnUpdatingUniqueStoreName(Long storeId, String name, String city, String street) {
                // given
                Store beforeUpdate = getStore(storeId, stores);
                String initialUrl = beforeUpdate.getImage().get().getImageUrl();
                StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
                // when
                var patchResponse = patchRequest("/api/store/" + storeId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequest("/api/store/image", Map.of("name", beforeUpdate.getName()));
                Integer count = jdbcTemplate.queryForObject
                        ("SELECT count(*) FROM store_image WHERE store_name = ?",
                                Integer.class, beforeUpdate.getName());
                // then
                assertIsError(getResponse.getBody(),
                        HttpStatus.NOT_FOUND,
                        "Unable to find store of '%s' name".formatted(beforeUpdate.getName()),
                        "/api/store/image");
                assertThat(count)
                        .withFailMessage("'%s' was found in 'store_image' table".formatted(beforeUpdate.getName()))
                        .isEqualTo(0);
                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(initialUrl) == null);
                assertThat(getBufferedImageFromWeb(initialUrl))
                        .withFailMessage("Image was supposed to be deleted from remote")
                        .isNull();
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "2, Lubi, Braniewo, ul. Dworcowa 21",
                    "9, Carrefour, Katowice, null"
            }, nullValues = "null")
            @DisplayName("PATCH: '/api/store/{store_id}' updating store name attaches image")
            @DirtiesContext
            public void shouldAttachImageIfUpdatingToExistingBrandWithImage(Long storeId, String name, String city, String street) {
                // given
                StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
                // when
                var patchResponse = patchRequest("/api/store/" + storeId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                StoreResponseDTO actual = toModel(patchResponse.getBody(), StoreResponseDTO.class);
                // then
                assertThat(actual.getImage())
                        .withFailMessage("Image was supposed to exist")
                        .isNotNull();
            }

            @ParameterizedTest
            @CsvSource(value = {
                    "5, null, null, ul. Dworcowa 101",
                    "8, Carrefour, Gdansk, al. Hallera 12"
            }, nullValues = "null")
            @DisplayName("PATCH: '/api/store/{store_id}' not updating name does not change image")
            @DirtiesContext
            public void shouldNotInterfereWithImageIfNameWasNotChanged(Long storeId, String name, String city, String street) {
                // given
                StoreUpdateDTO request = createStoreUpdateRequest(name, city, street);
                BufferedImage initialImage = getBufferedImageFromWeb(getStoreImage(storeId, stores).getImageUrl());
                // when
                var patchResponse = patchRequest("/api/store/" + storeId, request);
                assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                StoreResponseDTO actual = toModel(patchResponse.getBody(), StoreResponseDTO.class);
                BufferedImage actualImage = getBufferedImageFromWeb(actual.getImage().getUrl());
                // then
                assertThat(actualImage)
                        .withFailMessage("Image was removed")
                        .isNotNull();
                BufferedImageAssert.assertThat(actualImage)
                        .isEqualTo(initialImage);
            }
        }

        @Nested
        @TestMethodOrder(MethodOrderer.Random.class)
        @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
        class DeleteRequests {

            private List<Store> stores;

            @Autowired
            public DeleteRequests(List<Store> stores) {
                this.stores = stores;
            }

            @ParameterizedTest
            @ValueSource(longs = {1, 8})
            @DisplayName("DELETE: '/api/store/{store_id}'")
            @DirtiesContext
            public void deletingOneOfMultipleStoresShouldNotDeleteImageTest(Long storeId) {
                Store store = getStore(storeId.longValue(), stores);
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/" + storeId);
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequestAuth("admin", "admin", "/api/store");
                List<String> actualURLs =
                        toModelList(getResponse.getBody(), StoreResponseDTO.class)
                                .stream()
                                .filter(storeResponse ->
                                        storeResponse.getName().equals(store.getName()))
                                .map(storeResponse -> storeResponse.getImage().getUrl())
                                .toList();
                BufferedImage actualComp = getBufferedImageFromWeb(actualURLs.get(0));
                // then
                String expectedURL = store.getImage().get().getImageUrl();
                BufferedImage expectedComp = getBufferedImageFromWeb(expectedURL);
                assertThat(actualURLs).containsOnly(expectedURL);
                BufferedImageAssert.assertThat(actualComp).isEqualTo(expectedComp);
            }

            @ParameterizedTest
            @ValueSource(longs = {4, 5})
            @DisplayName("DELETE: '/api/store/{store_id}' last-entity-standing")
            @DirtiesContext
            public void deletingLastEntityStandingShouldRemoveImageTest(Long storeId) {
                Store store = getStore(storeId.longValue(), stores);
                // when
                var deleteResponse = deleteRequestAuth("admin", "admin", "/api/store/" + storeId);
                assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                var getResponse = getRequestAuth("admin", "admin", "/api/store");
                List<String> actualURLs =
                        toModelList(getResponse.getBody(), StoreResponseDTO.class)
                                .stream()
                                .filter(storeResponse ->
                                        storeResponse.getName().equals(store.getName()))
                                .map(storeResponse -> storeResponse.getImage().getUrl())
                                .toList();
                // then
                assertThat(actualURLs)
                        .withFailMessage("Store was supposed to be deleted")
                        .isEmpty();
                String urlExpectedToNotExist = store.getImage().get().getImageUrl();
                await().atMost(pollIntervalsUntil, TimeUnit.MILLISECONDS)
                        .pollInterval(pollIntervals, TimeUnit.MILLISECONDS)
                        .until(() -> getBufferedImageFromWeb(urlExpectedToNotExist) == null);
                assertThat(getBufferedImageFromWeb(urlExpectedToNotExist))
                        .withFailMessage("Image was not deleted remotely")
                        .isNull();
            }
        }
    }
}