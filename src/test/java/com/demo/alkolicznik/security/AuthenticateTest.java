package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.security.AuthRequestDTO;
import com.demo.alkolicznik.dto.security.AuthResponseDTO;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.JsonUtils.createAuthRequest;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.createTokenCookie;
import static com.demo.alkolicznik.utils.requests.JWTRequests.getRequestJWT;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "test-controller", "no-vaadin"})
@TestClassOrder(ClassOrderer.Random.class)
public class AuthenticateTest {

    private static final HttpStatusCode TEST_ENDPOINT_OK_CODE = HttpStatus.I_AM_A_TEAPOT;
    private static String TEST_ENDPOINT_BODY = "Access granted, ";

    @Nested
    @TestClassOrder(ClassOrderer.Random.class)
    class Authenticate {

        @ParameterizedTest
        @CsvSource({
                "admin, admin, admin",
                "heckler, Gaziwubalu66, user",
                "kacprox07, Kodobarazi20, accountant"
        })
        @DisplayName("POST: '/api/auth/authenticate'")
        public void shouldBeAuthenticatedOnCorrectCredentialsWithCookie(String username, String password, String role) {
            // given
            AuthRequestDTO credentials = createAuthRequest(username, password);

            // when
            var response = postRequest("/api/auth/authenticate", credentials);
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            String actualJson = response.getBody();
            AuthResponseDTO actual = toModel(actualJson, AuthResponseDTO.class);

            // then
            response = getRequestJWT("/api/test/" + role, createTokenCookie(actual.getToken()));
            System.out.println(response.getBody());
            assertThat(response.getStatusCode()).isEqualTo(TEST_ENDPOINT_OK_CODE);
            assertThat(response.getBody()).isEqualTo(TEST_ENDPOINT_BODY + role + "!");
        }

//        public void shouldBeAuthenticatedOnCorrectCredentials(String username, String password) {
//
//        }
    }

}
