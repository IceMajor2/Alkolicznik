package com.demo.alkolicznik.api;

import com.demo.alkolicznik.TestConfig;
import com.demo.alkolicznik.dto.requests.UserRequestDTO;
import com.demo.alkolicznik.dto.responses.UserResponseDTO;
import com.demo.alkolicznik.models.Roles;
import com.demo.alkolicznik.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Random;

import static com.demo.alkolicznik.utils.JsonUtils.*;
import static com.demo.alkolicznik.utils.CustomAssertions.*;
import static com.demo.alkolicznik.utils.ResponseUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class UserTests {

    @Autowired
    private List<String> correctPasswords;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getRandomPassword() {
        Random random = new Random();
        return correctPasswords.get(random.nextInt(correctPasswords.size()));
    }

    @Test
    @DisplayName("Create a user")
    @DirtiesContext
    public void createUserTest() {
        String pass = getRandomPassword();
        var postResponse = postRequest("/api/auth/signup",
                createUserRequest("john", pass));
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        String actualJson = postResponse.getBody();
        UserResponseDTO actual = toModel(actualJson, UserResponseDTO.class);
        assertPasswordHashedJson(pass, actualJson);

        UserResponseDTO expected = createUserResponse("john", Roles.USER);
        String expectedJson = toJsonString(expected);

        assertThat(actual).isEqualTo(expected);
        assertThat(actualJson).isEqualTo(expectedJson);

        // TODO: Fetch the newly-created user.
    }
}
