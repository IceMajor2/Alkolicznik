package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.responses.UserResponseDTO;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Random;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertPasswordHashed;
import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class UserTests {

    @Autowired
    private List<String> correctPasswords;

    @Autowired
    private List<User> users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getRandomPassword() {
        Random random = new Random();
        return correctPasswords.get(random.nextInt(correctPasswords.size()));
    }

    @Test
    @DisplayName("POST: '/api/auth/signup'")
    @DirtiesContext
    public void whenCreateValidUser_thenSuccessTest() {
        String pass = getRandomPassword();
        var postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("john", pass));
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Fetch user directly from the HSQL database.
        User userInDb = TestUtils.fetchUser(users.size() + 1);
        assertThat(userInDb)
                .withFailMessage("User was not saved in database")
                .isNotNull();

        String actualJson = postResponse.getBody();
        UserResponseDTO actual = toModel(actualJson, UserResponseDTO.class);

        UserResponseDTO expected = createUserResponse("john", Roles.USER);
        String expectedJson = toJsonString(expected);

        assertPasswordHashed(pass, userInDb.getPassword());
        assertThat(actual).isEqualTo(expected);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    @DisplayName("Assert password encoding")
    @DirtiesContext
    public void whenCreateUser_thenPasswordMustBeEncodedTest() {
        String pass = getRandomPassword();
        var postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("george", pass));
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User userInDb = TestUtils.fetchUser(users.size() + 1);

        assertPasswordHashed(pass, userInDb.getPassword());
    }

    @Test
    @DisplayName("Assert password conditions constraints")
    @DirtiesContext
    public void whenCreateUser_thenCheckPasswordIsStrongEnoughTest() {
        String pass = "stringstrin";
        var postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("user01", pass));

        String jsonResponse = postResponse.getBody();

        assertIsError(jsonResponse,
                HttpStatus.BAD_REQUEST,
                "Password length must be at least 12 characters long",
                "/api/auth/signup");

        pass = "stringstring";
        postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("user01", pass));
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("POST: '/api/auth/signup' [USER_EXISTS]")
    public void whenCreateUserThatAlreadyExists_thenThrowExceptionTest() {
        String pass = getRandomPassword();
        var postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("jacek", pass));

        String jsonResponse = postResponse.getBody();

        assertIsError(jsonResponse,
                HttpStatus.CONFLICT,
                "Username is already taken",
                "/api/auth/signup");
    }
}
