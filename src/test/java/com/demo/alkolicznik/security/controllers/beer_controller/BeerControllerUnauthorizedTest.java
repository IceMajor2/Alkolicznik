package com.demo.alkolicznik.security.controllers.beer_controller;

import com.demo.alkolicznik.dto.beer.BeerDeleteRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.matchers.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.BasicAuthRequests.*;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Random.class)
@ActiveProfiles({"main", "no-vaadin"})
public class BeerControllerUnauthorizedTest {

    @Test
    @DisplayName("[ANON]: restricted GET '/api/beer'")
    public void anonRestrictedGetEndpointsTest() {
        String endpoint = "/api/beer";
        var response = getRequest(endpoint);
        String actualJson = response.getBody();

        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Ksiazece, IPA, null",
            "'', null, -1.0"
    }, nullValues = "null")
    @DisplayName("[ANON]: restricted POST '/api/beer'")
    public void anonRestrictedPostEndpointsTest(String brand, String type, Double volume) {
        // given
        String endpoint = "/api/beer";
        BeerRequestDTO request = createBeerRequest(brand, type, volume);
        // when
        var response = postRequest(endpoint, request);
        String actualJson = response.getBody();

        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "4, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
            "9218, null, IPA, 0",
            "3, null, null, null"
    }, nullValues = "null")
    @DisplayName("[ANON]: restricted PUT '/api/beer/{beer_id}'")
    public void anonRestrictedPutEndpointsTest(Long beerId, String brand,
                                               String type, Double volume) {
        // given
        String endpoint = "/api/beer/" + beerId;
        BeerRequestDTO request = createBeerRequest(brand, type, volume);

        // when
        var response = putRequest(endpoint, request);
        String actualJson = response.getBody();

        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2, Miloslaw, null, 0.33",
            "-5, Namyslow, null, -0.5",
            "3, null, null, null"
    }, nullValues = "null")
    @DisplayName("[ANON]: restricted PATCH '/api/beer/{beer_id}'")
    public void anonRestrictedPatchRequestsTest(Long beerId, String brand,
                                                String type, Double volume) {
        // given
        String endpoint = "/api/beer/" + beerId;
        BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

        // when
        var response = patchRequest(endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Ksiazece, Zlote pszeniczne, null",
            "null, Biale, 0",
            "null, null, null"
    }, nullValues = "null")
    @DisplayName("[ANON]: restricted DELETE '/api/beer'")
    public void anonRestrictedDeleteByObjectRequestsTest(String brand, String type,
                                                         Double volume) {
        // given
        String endpoint = "/api/beer";
        BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

        // when
        var response = deleteRequest(endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @ValueSource(longs = {3, -5})
    @DisplayName("[ANON]: restricted DELETE '/api/beer/{beer_id}'")
    public void anonRestrictedDeleteByIdRequestsTest(Long beerId) {
        String endpoint = "/api/beer/" + beerId;
        // when
        var response = deleteRequest(endpoint);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ValueSource(strings = "user")
    @DisplayName("[USER]: restricted GET endpoints")
    public void unauthorizedRestrictedEndpointsTest(String credentials) {
        String endpoint = "/api/beer";
        // password is same as username
        var response = getRequestAuth(credentials, credentials, endpoint);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user, Ksiazece, IPA, null",
            "user, '', null, -1.0"
    }, nullValues = "null")
    @DisplayName("[USER]: restricted POST endpoints")
    public void unauthorizedRestrictedPostEndpointsTest(String credentials, String brand, String type,
                                                        Double volume) {
        // given
        String endpoint = "/api/beer";
        BeerRequestDTO request = createBeerRequest(brand, type, volume);
        // when
        var response = postRequestAuth(credentials, credentials, endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user, 3, Manufaktura Piwna, Piwo na miodzie gryczanym, null",
            "user, -12564, null, IPA, 0",
            "user, 1, null, null, null"
    }, nullValues = "null")
    @DisplayName("[USER]: restricted PUT endpoints")
    public void unauthorizedRestrictedPutEndpointsTest(String credentials, Long beerId,
                                                       String brand, String type, Double volume) {
        // given
        String endpoint = "/api/beer/" + beerId;
        BeerRequestDTO request = createBeerRequest(brand, type, volume);

        // when
        var response = putRequestAuth(credentials, credentials, endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user, 2, Miloslaw, null, 0.33",
            "user, -5, Namyslow, null, -0.5",
            "user, 3, null, null, null"
    }, nullValues = "null")
    @DisplayName("[USER]: restricted PATCH requests")
    public void unauthorizedRestrictedPatchRequestsTest(String credentials, Long beerId,
                                                        String brand, String type, Double volume) {
        // given
        String endpoint = "/api/beer/" + beerId;
        BeerUpdateDTO request = createBeerUpdateRequest(brand, type, volume);

        // when
        var response = patchRequestAuth(credentials, credentials, endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user, Ksiazece, Zlote pszeniczne, null",
            "user, null, Biale, 0",
            "user, null, null, null"
    }, nullValues = "null")
    @DisplayName("[USER]: restricted DELETE by object requests")
    public void unauthorizedRestrictedDeleteByObjectRequestsTest(String credentials, String brand,
                                                                 String type, Double volume) {
        // given
        String endpoint = "/api/beer";
        BeerDeleteRequestDTO request = createBeerDeleteRequest(brand, type, volume);

        // when
        var response = deleteRequestAuth(credentials, credentials, endpoint, request);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "user, 1",
            "user, -5"
    })
    @DisplayName("[USER]: restricted DELETE by id requests")
    public void unauthorizedRestrictedDeleteByIdRequestsTest(String credentials, Long beerId) {
        // when
        String endpoint = "/api/beer/" + beerId;
        var response = deleteRequestAuth(credentials, credentials, endpoint);
        String actualJson = response.getBody();

        // then
        assertIsError(actualJson,
                HttpStatus.NOT_FOUND,
                "Resource not found",
                endpoint);
    }
}
