package com.demo.alkolicznik.utils.requests;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static com.demo.alkolicznik.api.AdminTests.mockMvc;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.TestUtils.buildURI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class MockRequests {

    public static ResultActions mockGetRequest(String url) {
        try {
            return mockMvc.perform(get(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockPutRequest(String url, Object request) {
        try {
            return mockMvc.perform(
                    put(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockPutRequest(String url, Map<String, ?> parameters, Object request) {
        String urlTemplate = buildURI(url, parameters);
        try {
            return mockMvc.perform(
                    put(urlTemplate)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockDeleteRequest(String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        try {
            return mockMvc.perform(
                    delete(urlTemplate)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockDeleteRequest(Object request, String url) {
        try {
            return mockMvc.perform(
                    delete(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockDeleteRequest(String url) {
        try {
            return mockMvc.perform(
                    delete(url)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
