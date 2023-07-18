package com.demo.alkolicznik.api;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import com.demo.alkolicznik.dto.responses.ImageModelResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import static com.demo.alkolicznik.utils.CustomAssertions.assertMockRequest;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static com.demo.alkolicznik.utils.requests.MockRequests.mockPostRequest;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.getRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@AutoConfigureMockMvc
public class ImageModelTests {

    public static final String IMG_TRANSFORMED_URL = "https://ik.imagekit.io/icemajor/tr:n-get_beer/test/beer/";

    @Autowired
    private List<Beer> beers;

    @Autowired
    private ResourceLoader resourceLoader;

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
        // TODO: Make a stub / mock of service that sends the images to hosting (so that they are not actually sent).
        @Test
        @DisplayName("POST: '/api/beer/'")
        @DirtiesContext
        @WithUserDetails("admin")
        public void whenAddingBeerWithImage_thenReturnOKTest() throws IOException {
            var expected = createBeerResponse(
                    beers.size() + 1, "Kasztelan",
                    "Niepasteryzowane", 0.5,
                    createImageResponse("kasztelan-niepasteryzowane-0.5.png")
            );
            var expectedJson = toJsonString(expected);
            var request = createBeerRequest("Kasztelan", "Niepasteryzowane",
                    null, getRawPathToImage("kasztelan-niepasteryzowane.png"));
            var actualJson = assertMockRequest(mockPostRequest("/api/beer", request),
                    HttpStatus.CREATED,
                    expectedJson);
            var actual = toModel(actualJson, BeerResponseDTO.class);

            assertThat(actual).isEqualTo(expected);
            assertThat(actualJson).isEqualTo(expectedJson);
        }
    }

    private String getRawPathToImage(String imageFilename) throws IOException {
        URI uri = resourceLoader.getResource("classpath:data_img/" + imageFilename).getURI();
        String rawPath = Paths.get(uri).toAbsolutePath().toString();
        return rawPath;
    }
}
