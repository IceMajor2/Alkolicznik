package com.demo.alkolicznik.utils.requests;

import com.demo.alkolicznik.api.BeerPriceTests;
import com.demo.alkolicznik.api.BeerTests;
import com.demo.alkolicznik.api.ImageModelTests;
import com.demo.alkolicznik.api.StoreTests;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static com.demo.alkolicznik.utils.TestUtils.buildURI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class MockRequests {

    private static MockMvc mockMvc;

    private static void initMockMvc() {
        if (StoreTests.mockMvc != null) {
            mockMvc = StoreTests.mockMvc;
            return;
        }
        if (ImageModelTests.mockMvc != null) {
            mockMvc = ImageModelTests.mockMvc;
            return;
        }
        if (BeerTests.mockMvc != null) {
            mockMvc = BeerTests.mockMvc;
            return;
        }
        if (BeerPriceTests.mockMvc != null) {
            mockMvc = BeerPriceTests.mockMvc;
            return;
        }
    }

    public static ResultActions mockGetRequest(String url) {
        initMockMvc();
        try {
            return mockMvc.perform(get(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockGetRequest(String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return mockGetRequest(urlTemplate);
    }

    public static ResultActions mockPutRequest(String url, Object request) {
        initMockMvc();
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

    public static ResultActions mockPostRequest(String url, Object request) {
        initMockMvc();
        try {
            return mockMvc.perform(
                    post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockPutRequest(String url, Map<String, ?> parameters, Object request) {
        initMockMvc();
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
        initMockMvc();
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
        initMockMvc();
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
        initMockMvc();
        try {
            return mockMvc.perform(
                    delete(url)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
