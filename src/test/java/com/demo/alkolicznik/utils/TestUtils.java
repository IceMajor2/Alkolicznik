package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.models.Beer;
import com.demo.alkolicznik.models.Store;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Component
public class TestUtils {

    public static Beer getBeer(Long beerId, List<Beer> beers) {
        for (Beer beer : beers) {
            if (beer.getId() == beerId) {
                return beer;
            }
        }
        return null;
    }

    public static Store getStore(Long storeId, List<Store> stores) {
        for (Store store : stores) {
            if (store.getId() == storeId) {
                return store;
            }
        }
        return null;
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
