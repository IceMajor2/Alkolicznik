package com.demo.alkolicznik.security.controllers.store_controller;

import com.demo.alkolicznik.dto.store.StoreRequestDTO;
import com.demo.alkolicznik.dto.store.StoreUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomErrorAssertion.assertIsError;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Random.class)
@ActiveProfiles({"main", "no-vaadin"})
public class StoreControllerUnauthorizedTest {

    private static final String STORE_ENDPOINT = "/api/store";

    @Test
    @DisplayName("[ANON]: restricted GET '/api/store'")
    public void anonRestrictedGetEndpointTest() {
        var response = getRequest(STORE_ENDPOINT);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT);
    }

    @ParameterizedTest
    @MethodSource("com.demo.alkolicznik.utils.Parameters#validAndInvalidStorePostRequests")
    @DisplayName("[ANON]: restricted POST '/api/beer'")
    public void anonRestrictedPostEndpointTest(StoreRequestDTO request) {
        var response = postRequest(STORE_ENDPOINT, request);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT);
    }

    @ParameterizedTest
    @MethodSource("com.demo.alkolicznik.utils.Parameters#validAndInvalidStorePutRequests")
    @DisplayName("[ANON]: restricted PUT '/api/beer/{beer_id}'")
    public void anonRestirctedPutEndpointTest(Long beerId, StoreRequestDTO request) {
        var response = putRequest(STORE_ENDPOINT + '/' + beerId, request);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT + '/' + beerId);
    }

    @ParameterizedTest
    @MethodSource("com.demo.alkolicznik.utils.Parameters#validAndInvalidStorePatchRequests")
    @DisplayName("[ANON]: restricted PATCH '/api/beer/{beer_id}'")
    public void anonRestrictedPatchEndpointTest(Long beerId, StoreUpdateDTO request) {
        var response = putRequest(STORE_ENDPOINT + '/' + beerId, request);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT + '/' + beerId);
    }

    @ParameterizedTest
    @MethodSource("com.demo.alkolicznik.utils.Parameters#validAndInvalidStoreDeleteRequests")
    @DisplayName("[ANON]: restricted DELETE '/api/beer'")
    public void anonRestrictedDeleteEndpointTest(StoreRequestDTO request) {
        var response = deleteRequest(STORE_ENDPOINT, request);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT);
    }

    @ParameterizedTest
    @ValueSource(longs = {-5, 5, 8534289})
    @DisplayName("[ANON]: restricted DELETE '/api/beer'")
    public void anonRestrictedDeleteEndpointTest(Long beerId) {
        var response = deleteRequest(STORE_ENDPOINT + '/' + beerId);

        assertIsError(response.getBody(), HttpStatus.NOT_FOUND, "Resource not found", STORE_ENDPOINT + '/' + beerId);
    }
}
