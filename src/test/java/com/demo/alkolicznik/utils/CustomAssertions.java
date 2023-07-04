package com.demo.alkolicznik.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomAssertions {

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

    public static void assertIsError(ResultActions mockResponse,
                                                    HttpStatus expectedStatus,
                                                    String expectedMessage,
                                                    String expectedPath) {
        try {
            String actual = mockResponse.andReturn().getResponse().getContentAsString();
            assertIsError(actual, expectedStatus, expectedMessage, expectedPath);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
}
