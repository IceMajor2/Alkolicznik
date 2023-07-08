package com.demo.alkolicznik.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class CustomAssertions {

    public static PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        CustomAssertions.passwordEncoder = passwordEncoder;
    }

    public static String assertMockRequest(ResultActions mockResponse,
                                           HttpStatus expectedStatus,
                                           String expectedJson) {
        String actualJson = null;
        try {
            actualJson = mockResponse
                    .andExpect(status().is(expectedStatus.value()))
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return actualJson;
    }

    /**
     * Helper function for asserting that the response is an error.
     *
     * @param actual          received response as {@code String}
     * @param expectedStatus
     * @param expectedMessage
     * @param expectedPath
     * @throws Exception
     */
    public static void assertIsError(String actual,
                                     HttpStatus expectedStatus,
                                     String expectedMessage,
                                     String expectedPath) {
        JSONObject object = JsonUtils.getJsonObject(actual);
        assertIsError(object, expectedStatus, expectedMessage, expectedPath);
    }

    /**
     * Helper function for asserting that the response is an error.
     *
     * @param actual          received response as {@code JSONObject}
     * @param expectedStatus
     * @param expectedMessage
     * @param expectedPath
     */
    public static void assertIsError(JSONObject actual,
                                     HttpStatus expectedStatus,
                                     String expectedMessage,
                                     String expectedPath) {

        try {
            assertThat(actual.getInt("status")).isEqualTo(expectedStatus.value());
        } catch (JSONException e) {
            fail("'status' key is not present");
        }
        try {
            assertThat(actual.getString("message")).isEqualTo(expectedMessage);
        } catch (JSONException e) {
            fail("'message' key is not present");
        }
        try {
            assertThat(actual.getString("error")).isEqualTo(expectedStatus.getReasonPhrase());
        } catch (JSONException e) {
            fail("'error' key is not present");
        }
        try {
            assertThat(actual.getString("path")).isEqualTo(expectedPath);
        } catch (JSONException e) {
            fail("'path' key is not present");
        }
        try {
            assertThat(actual.getString("timestamp")).isNotNull();
        } catch (JSONException e) {
            fail("'timestamp' key is not present");
        }
        assertThat(actual.length()).isEqualTo(5);
    }

    public static void assertPasswordHashed(String rawPassword, String encodedPassword) {
        assertThat(rawPassword).withFailMessage("The provided 'raw password' is null").isNotNull();
        assertThat(encodedPassword).withFailMessage("The encoded password is null").isNotNull();
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword))
                .withFailMessage("The password's hash does not stand for request's raw one")
                .isTrue();
    }
}
