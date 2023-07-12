package com.demo.alkolicznik.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.demo.alkolicznik.api.AdminTests.mockMvc;
import static com.demo.alkolicznik.utils.JsonUtils.toJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class ResponseTestUtils {

    private static TestRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        ResponseTestUtils.restTemplate = restTemplate;
    }

    /**
     * Helper method for sending an {@code HTTP GET}
     * request through {@code TestRestTemplate}.
     *
     * @param url           url of target request
     * @param pathVariables url variables
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> getRequest(String url, Object... pathVariables) {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(url, String.class, pathVariables);
        return getResponse;
    }

    /**
     * Helper method for sending an {@code HTTP GET}
     * request through {@code TestRestTemplate} with parameters in URL.
     *
     * @param url        url of target request
     * @param parameters parameters as {@code Map}
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> getRequest(String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> getResponse = restTemplate
                .exchange(urlTemplate, HttpMethod.GET, HttpEntity.EMPTY, String.class, parameters);
        return getResponse;
    }

    public static ResponseEntity<String> getRequest(String url, Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(urlTemplate, String.class, pathVariables);
        return getResponse;
    }

    /**
     * Helper method for sending an {@code HTTP POST}
     * request through {@code TestRestTemplate}.
     *
     * @param url           url of target request
     * @param requestObject object to send to url
     * @param pathVariables url variables
     * @return {@ResponseEntity<String>}
     */
    public static ResponseEntity<String> postRequest(String url, Object requestObject, Object... pathVariables) {
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity(url, requestObject, String.class, pathVariables);
        return postResponse;
    }

    public static ResponseEntity<String> postRequest(String url, Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);
        ResponseEntity<String> postResponse = restTemplate
                .postForEntity(urlTemplate, null, String.class, pathVariables);
        return postResponse;
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password, String url, Object request, Object... pathVariables) {
        ResponseEntity<String> putResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.PUT, new HttpEntity(request), String.class, pathVariables);
        return putResponse;
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password, String url, Object request,
                                                        Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> putResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(urlTemplate, HttpMethod.PUT, new HttpEntity(request), String.class, pathVariables);
        return putResponse;
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String password, String url, Object... pathVariables) {
        ResponseEntity<String> deleteResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, String.class, pathVariables);
        return deleteResponse;
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String password, String url, Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> deleteResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(urlTemplate, HttpMethod.DELETE, HttpEntity.EMPTY, String.class, pathVariables);
        return deleteResponse;
    }

    public static ResultActions mockGetRequest(String url) {
        try {
            return mockMvc.perform(get(url));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockPutRequest(String url, Object request, Object... pathVariables) {
        try {
            return mockMvc.perform(
                    put(url, pathVariables)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockPutRequest(String url, Object request, Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);
        try {
            return mockMvc.perform(
                    put(urlTemplate, pathVariables)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJsonString(request))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockDeleteRequest(String url, Object... pathVariables) {
        try {
            return mockMvc.perform(
                    delete(url, pathVariables)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultActions mockDeleteRequest(String url, Map<String, ?> parameters, Object... pathVariables) {
        String urlTemplate = buildURI(url, parameters);
        try {
            return mockMvc.perform(
                    delete(urlTemplate, pathVariables)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildURI(String uriString, Map<String, ?> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
        for (var entry : parameters.entrySet()) {
            builder
                    .queryParam(entry.getKey(), entry.getValue());
        }
        String urlTemplate = builder.encode().toUriString();
        return urlTemplate;
    }
}