package com.demo.alkolicznik.api;

import java.util.List;
import java.util.Random;

import com.demo.alkolicznik.dto.user.UserResponseDTO;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import com.demo.alkolicznik.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.demo.alkolicznik.utils.CustomAssertions.assertIsError;
import static com.demo.alkolicznik.utils.CustomAssertions.assertPasswordHashed;
import static com.demo.alkolicznik.utils.JsonUtils.createUserRequest;
import static com.demo.alkolicznik.utils.JsonUtils.createUserResponse;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.JsonUtils.toModel;
import static com.demo.alkolicznik.utils.requests.AuthenticatedRequests.postRequestAuth;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("main")
public class UserTest {

	@Autowired
	private List<User> users;

	@Autowired
	private PasswordEncoder passwordEncoder;

    private String[] correctPasswords;

	@Autowired
	public void setCorrectPasswords(ApplicationContext context) {
		String[] bean = (String[]) context.getBean("correctPasswords");
		this.correctPasswords = bean;
	}

    private String getRandomPassword() {
        Random random = new Random();
		return correctPasswords[random.nextInt(correctPasswords.length)];
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
        User userInDb = TestUtils.getUser(users.size() + 1, users);
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
        // no need to create new user: just fetch already existing from db. [FIX_HERE]
        String pass = getRandomPassword();
        var postResponse = postRequestAuth("admin", "admin",
                "/api/auth/signup",
                createUserRequest("george", pass));
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User userInDb = TestUtils.getUser(users.size() + 1, users);

        assertPasswordHashed(pass, userInDb.getPassword());
    }

    @Test
    @DisplayName("Assert password conditions constraints")
    @DirtiesContext
    // no need to create new user: just fetch already existing from db. [FIX_HERE]
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
