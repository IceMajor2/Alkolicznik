package com.demo.alkolicznik.security.beer_controller.unauthorized;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisabledVaadinContext.class)
@TestMethodOrder(MethodOrderer.Random.class)
@ActiveProfiles("main")
public class BeerControllerAuthorizedTest {

    @ParameterizedTest
    @ValueSource(strings = {"accountant", "admin"})
    @DisplayName("[ACC/ADMIN]: restricted GET endpoints")
    public void authorizedRestrictedEndpointsTest(String credentials) {
        String endpoint = "/api/beer";
        var response = getRequestAuth(credentials, credentials, endpoint);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "accountant, Lomza, Jasne, 0.33",
            "admin, Manufaktura Piwna, Piwo na miodzie gryczanym, null"
    }, nullValues = "null")
    @DisplayName("[ACC/ADMIN]: restricted POST endpoints")
    @DirtiesContext
    public void authorizedRestrictedPostEndpointsTest(String credentials,
                                                      String brand, String type, Double volume) {
        String endpoint = "/api/beer";
        BeerRequestDTO request = createBeerRequest(brand, type, volume);

        var response = postRequestAuth(credentials, credentials, endpoint, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "admin, 3, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
            "accountant, 5, Tyskie, IPA, 0.33"
    }, nullValues = "null")
    @DisplayName("[ACC/ADMIN]: restricted PUT endpoints")
    @DirtiesContext
    public void authorizedRestrictedPutEndpointsTest(String credentials, Long beerId,
                                                     String brand, String type, Double volume) {
        String endpoint = "/api/beer/" + beerId;
        BeerRequestDTO request = createBeerRequest(brand, type, volume);

        var response = putRequestAuth(credentials, credentials, endpoint, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "admin, 2, Miloslaw, null, 0.33",
            "accountant, 1, null, Porter, null"
    }, nullValues = "null")
    @DisplayName("[ACC/ADMIN]: restricted PATCH requests")
    @DirtiesContext
    public void authorizedRestrictedPatchRequestsTest(String credentials, Long beerId,
                                                      String brand, String type, Double volume) {
        String endpoint = "/api/beer/" + beerId;
        BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

        var response = patchRequestAuth(credentials, credentials, endpoint, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "admin, Ksiazece, Zlote pszeniczne, null",
            "accountant, Zubr, null, 0.5"
    }, nullValues = "null")
    @DisplayName("[ACC/ADMIN]: restricted DELETE by object requests")
    @DirtiesContext
    public void authorizedRestrictedDeleteByObjectRequestsTest(String credentials,
                                                               String brand, String type, Double volume) {
        // given
        String endpoint = "/api/beer";
        BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

        // when
        var response = deleteRequestAuth(credentials, credentials, endpoint, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "admin, 7",
            "accountant, 4"
    })
    @DisplayName("[ACC/ADMIN]: restricted DELETE by id requests")
    @DirtiesContext
    public void authorizedRestrictedDeleteByIdRequestsTest(String credentials, Long beerId) {
        // when
        String endpoint = "/api/beer/" + beerId;
        var response = deleteRequestAuth(credentials, credentials, endpoint);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
