package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.BeerPriceResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static com.demo.alkolicznik.TestUtils.*;
import static com.demo.alkolicznik.TestUtils.toJsonString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class BeerPriceTests {

    @Test
    @DisplayName("Valid add beer to store")
    @DirtiesContext
    public void addBeerToStoreTest() {
        var postResponse = postRequest("/api/store/{id}/beer",
                createBeerPriceRequest("Perla Chmielowa Pils", 3.69),
                2);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String actualJson = postResponse.getBody();

        BeerPriceResponseDTO expected = createBeerPriceResponse(
                createBeerResponseDTO(1L, "Perla Chmielowa Pils", 0.5),
                createStoreResponseDTO(2L, "Biedronka", "Olsztyn", "ul. Sikorskiego-Wilczynskiego 12"), 3.69
        );
        String expectedJson = toJsonString(expected);

        assertThat(actualJson).isEqualTo(expectedJson);
    }
}
