package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.responses.ImageResponseDTO;
import com.demo.alkolicznik.models.Beer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.ResponseTestUtils.getRequest;
import static com.demo.alkolicznik.utils.TestUtils.getBeer;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
//@AutoConfigureMockMvc
public class ImageTests {

    @Autowired
    private List<Beer> beers;

//    public static MockMvc mockMvc;
//
//    @Autowired
//    public void setMockMvc(MockMvc mockMvc) {
//        ImageTests.mockMvc = mockMvc;
//    }

    @Nested
    class GetRequests {

        @Test
        @DisplayName("Get beer image as accountant")
        public void whenUserGetsBeerImage_returnOKTest() {
            ImageResponseDTO expected = createImageResponse(getBeer(5L, beers).getImage());
            String expectedJson = toJsonString(expected);

            var response = getRequest("/api/beer/{id}/image", 5L);

            String actualJson = response.getBody();
            ImageResponseDTO actual = toModel(actualJson, ImageResponseDTO.class);

            assertThat(actualJson).isEqualTo(expectedJson);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
