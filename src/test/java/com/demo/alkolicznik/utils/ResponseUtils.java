package com.demo.alkolicznik.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.demo.alkolicznik.api.AdminApiTests.mockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ResponseUtils {

    private static TestRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        ResponseUtils.restTemplate = restTemplate;
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

    public static String assertMockGetRequest(String url,
                                                 HttpStatus expectedStatus,
                                                 String expectedJson) {
        String actualJson = null;
        try {
            actualJson = mockMvc.perform(get(url))
                    .andExpect(status().is(expectedStatus.value()))
                    .andExpect(content().json(expectedJson))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actualJson;
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
