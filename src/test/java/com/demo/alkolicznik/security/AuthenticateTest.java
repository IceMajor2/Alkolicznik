package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import com.demo.alkolicznik.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.demo.alkolicznik.utils.FindingUtils.getUserRoleLowerCase;
import static com.demo.alkolicznik.utils.JsonUtils.createAuthRequest;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.createTokenCookie;
import static com.demo.alkolicznik.utils.requests.JWTRequests.getRequestJWT;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "test-controller", "no-vaadin"})
@TestMethodOrder(MethodOrderer.Random.class)
public class AuthenticateTest {

    private static final HttpStatusCode TEST_ENDPOINT_OK_CODE = HttpStatus.I_AM_A_TEAPOT;
    private static String TEST_ENDPOINT_BODY = "Access granted, ";

    private List<User> users;

    @Autowired
    public AuthenticateTest(List<User> users) {
        this.users = users;
    }

    @ParameterizedTest
    @CsvSource({
            "admin, admin",
            "heckler, Gaziwubalu66",
            "kacprox07, Kodobarazi20"
    })
    @DisplayName("POST: '/api/auth/authenticate'")
    public void shouldBeAuthenticatedOnCorrectCredentialsWithCookie(String username, String password) {
        // given
        AuthRequestDTO credentials = createAuthRequest(username, password);
        String role = getUserRoleLowerCase(username, users);
        // when
        var response = postRequest("/api/auth/authenticate", credentials);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String actualJson = response.getBody();
        AuthResponseDTO actual = toModel(actualJson, AuthResponseDTO.class);
        // then
        response = getRequestJWT("/api/test/" + role, createTokenCookie(actual.getToken()));
        assertThat(response.getStatusCode()).isEqualTo(TEST_ENDPOINT_OK_CODE);
        assertThat(response.getBody()).isEqualTo(TEST_ENDPOINT_BODY + role + "!");
    }

    public void shouldBeAuthenticatedOnCorrectCredentials(String username, String password) {

    }
}
