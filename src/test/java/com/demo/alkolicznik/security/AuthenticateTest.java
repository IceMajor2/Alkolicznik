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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.demo.alkolicznik.config.profiles.TestControllerProfile.TEST_ENDPOINT_BODY;
import static com.demo.alkolicznik.config.profiles.TestControllerProfile.TEST_ENDPOINT_OK_CODE;
import static com.demo.alkolicznik.utils.CustomErrorAssertion.assertIsError;
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
    @DisplayName("POST: '/api/auth/authenticate' cookie")
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

    @ParameterizedTest
    @CsvSource({
            "admin, wrong_password",
            "kacprox07, haslo",
            "jacek, Cr1($dkao2,1-s;a"
    })
    @DisplayName("POST: '/api/auth/authenticate' wrong password")
    public void shouldReturn404OnWrongPassword(String username, String password) {
        // given
        AuthRequestDTO credentials = createAuthRequest(username, password);
        // when
        var response = postRequest("/api/auth/authenticate", credentials);
        // then
        assertIsError(response.getBody(),
                HttpStatus.NOT_FOUND,
                "Could not log in: wrong credentials",
                "/api/auth/authenticate"
        );
    }

    @ParameterizedTest
    @CsvSource({
            "krzyzak, bork!tomasz_nn!$63;mal",
            "xXx_dragon69, w44Rior1!!+_pap",
            "kris_PL, b33R!MyL0ve:)91!"
    })
    @DisplayName("POST: '/api/auth/authenticate' wrong credentials")
    public void shouldReturn404OnWrongCredentials(String username, String password) {
        // given
        AuthRequestDTO credentials = createAuthRequest(username, password);
        // when
        var response = postRequest("/api/auth/authenticate", credentials);
        // then
        assertIsError(response.getBody(),
                HttpStatus.NOT_FOUND,
                "Could not log in: wrong credentials",
                "/api/auth/authenticate"
        );
    }
}
