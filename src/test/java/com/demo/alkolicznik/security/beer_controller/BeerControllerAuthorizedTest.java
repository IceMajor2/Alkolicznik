package com.demo.alkolicznik.security.beer_controller;

import com.demo.alkolicznik.dto.beer.BeerRequestDTO;
import com.demo.alkolicznik.dto.beer.BeerUpdateDTO;
import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.security.services.AuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static com.demo.alkolicznik.utils.TestUtils.createTokenCookie;
import static com.demo.alkolicznik.utils.requests.JwtRequests.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "no-vaadin"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeerControllerAuthorizedTest {

    private static AuthService authService;

    private static final BeerRequestDTO VALID_POST_REQUEST = new BeerRequestDTO("Lomza", "Pelne", 0.33);
    private static final BeerUpdateDTO VALID_PATCH_REQUEST = new BeerUpdateDTO(null, "", 0.5);
    private static final BeerRequestDTO VALID_DELETE_REQUEST = new BeerRequestDTO("Ksiazece", "Zlote pszeniczne", null);
    private static String accountantToken;
    private static String adminToken;

    @Autowired
    public void setAuthService(AuthService authService) {
        BeerControllerAuthorizedTest.authService = authService;
    }

    @BeforeEach
    void setup() {
        accountantToken = authService.authenticate
                        (new AuthRequestDTO("accountant", "accountant"))
                .getToken();
        adminToken = authService.authenticate
                        (new AuthRequestDTO("admin", "admin"))
                .getToken();
    }

    private static Stream<Arguments> adminTokens() {
        return Stream.of(
                Arguments.of(accountantToken),
                Arguments.of(adminToken)
        );
    }

    @Test
    @Order(Ordered.HIGHEST_PRECEDENCE)
    void loadContext() {
        // This "test" is only for loading the context, so that
        // the method sources may be used successfully
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted GET '/api/beer'")
    public void authorizedRestrictedEndpointsTest(String adminToken) {
        String endpoint = "/api/beer";
        var response = getRequestJWT(endpoint, createTokenCookie(adminToken));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted POST '/api/beer'")
    @DirtiesContext
    public void authorizedRestrictedPostEndpointsTest(String adminToken) {
        String endpoint = "/api/beer";
        var response = postRequestJWT(endpoint, createTokenCookie(adminToken), VALID_POST_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted PUT '/api/beer/{beer_id}'")
    @DirtiesContext
    public void authorizedRestrictedPutEndpointsTest(String adminToken) {
        String endpoint = "/api/beer/" + 1;
        var response = putRequestJWT(endpoint, createTokenCookie(adminToken), VALID_POST_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted PATCH '/api/beer/{beer_id}'")
    @DirtiesContext
    public void authorizedRestrictedPatchRequestsTest(String adminToken) {
        String endpoint = "/api/beer/" + 3;
        var response = patchRequestJWT(endpoint, createTokenCookie(adminToken), VALID_PATCH_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted DELETE '/api/beer'")
    @DirtiesContext
    public void authorizedRestrictedDeleteByObjectRequestsTest(String adminToken) {
        String endpoint = "/api/beer";
        var response = deleteRequestJWT(endpoint, createTokenCookie(adminToken), VALID_DELETE_REQUEST);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("adminTokens")
    @DisplayName("[ACC/ADMIN]: restricted DELETE '/api/beer/{beer_id}'")
    @DirtiesContext
    public void authorizedRestrictedDeleteByIdRequestsTest(String adminToken) {
        String endpoint = "/api/beer/" + 6;
        var response = deleteRequestJWT(endpoint, createTokenCookie(adminToken));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
