package com.demo.alkolicznik.utils.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.demo.alkolicznik.utils.TestUtils.buildURI;

@Component
public class SimpleRequests {

    private static TestRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        SimpleRequests.restTemplate = restTemplate;
    }

    public static ResponseEntity<String> getRequest(String url) {
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(url, String.class);
        return getResponse;
    }

    public static ResponseEntity<String> getRequest(String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> getResponse = restTemplate
                .exchange(urlTemplate, HttpMethod.GET, HttpEntity.EMPTY, String.class, parameters);
        return getResponse;
    }

    public static ResponseEntity<String> postRequest(String url, Object request) {
        return restTemplate.postForEntity(url, request, String.class);
    }

    public static ResponseEntity<String> putRequest(String url, Object request) {
        return restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(request), String.class);
    }

    public static ResponseEntity<String> deleteRequest(String url) {
        return restTemplate
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
    }

    public static ResponseEntity<String> deleteRequest(String url, Object request) {
        return restTemplate
                .exchange(url, HttpMethod.DELETE, new HttpEntity<>(request), String.class);
    }
}
