package com.demo.alkolicznik.security;

import com.demo.alkolicznik.dto.security.SignupRequestDTO;
import com.demo.alkolicznik.dto.security.SignupResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.config.profiles.TestControllerProfile.TEST_ENDPOINT_BODY;
import static com.demo.alkolicznik.config.profiles.TestControllerProfile.TEST_ENDPOINT_OK_CODE;
import static com.demo.alkolicznik.utils.JsonUtils.createSignupRequest;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.TestUtils.createTokenCookie;
import static com.demo.alkolicznik.utils.requests.JWTRequests.getRequestJWT;
import static com.demo.alkolicznik.utils.requests.SimpleRequests.postRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"main", "test-controller", "no-vaadin"})
@TestMethodOrder(MethodOrderer.Random.class)
public class SignupTest {

    @ParameterizedTest
    @CsvSource({
            "slimeball, ekyqexD.pDng)B~",
            "pole, .J)&L=.IME)9",
            "nfshift, Wr8mLAx65qbJe0104OjNo8TT",
            "D3F3t!, WjwWmjrjAaheZg"
    })
    @DisplayName("POST: '/api/auth/signup'")
    public void shouldCreateNewUserWhenSignupWithRequirementsInCheck(String username, String password) {
        // given
        SignupRequestDTO request = createSignupRequest(username, password);
        // when
        var response = postRequest("/api/auth/signup", request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SignupResponseDTO actual = toModel(response.getBody(), SignupResponseDTO.class);
        String role = actual.getRole();
        String token = actual.getToken();
        // then
        response = getRequestJWT("/api/test/" + role, createTokenCookie(token));
        assertThat(response.getStatusCode()).isEqualTo(TEST_ENDPOINT_OK_CODE);
        assertThat(response.getBody()).isEqualTo(TEST_ENDPOINT_BODY + role + "!");
    }
}
