package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
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
            Image expected = getBeer(5L, beers).getImage();
            String expectedJson = toJsonString(expected);
            System.out.println(expected);

            var response = getRequest("/api/beer/image/{id}", 5L);

            String actualJson = response.getBody();
            Image actual = toModel(actualJson, Image.class);

            assertThat(actual).isEqualTo(expected);

        }
    }
}
