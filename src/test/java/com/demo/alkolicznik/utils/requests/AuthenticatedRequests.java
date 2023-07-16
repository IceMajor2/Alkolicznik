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
public class AuthenticatedRequests {

    private static TestRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(TestRestTemplate restTemplate) {
        AuthenticatedRequests.restTemplate = restTemplate;
    }

    public static ResponseEntity<String> getRequestAuth(String username, String password, String url) {
        return restTemplate
                .withBasicAuth(username, password)
                .getForEntity(url, String.class);
    }

    public static ResponseEntity<String> postRequestAuth(String username, String password, String url,
                                                         Object requestObject) {
        ResponseEntity<String> postResponse = restTemplate
                .withBasicAuth(username, password)
                .postForEntity(url, requestObject, String.class);
        return postResponse;
    }

    public static ResponseEntity<String> postRequestAuth(String username, String password, String url,
                                                         Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return restTemplate
                .withBasicAuth(username, password)
                .postForEntity(urlTemplate, null, String.class);
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password, String url, Object request) {
        ResponseEntity<String> putResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.PUT, new HttpEntity(request), String.class);
        return putResponse;
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password, String url,
                                                        Object request, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> putResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(urlTemplate, HttpMethod.PUT, new HttpEntity(request), String.class);
        return putResponse;
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String password, String url) {
        ResponseEntity<String> deleteResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        return deleteResponse;
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String password, String url,
                                                           Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);

        ResponseEntity<String> deleteResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(urlTemplate, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        return deleteResponse;
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String password, String url, Object request) {
        ResponseEntity<String> deleteResponse = restTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.DELETE, new HttpEntity<>(request), String.class);
        return deleteResponse;
    }
}
