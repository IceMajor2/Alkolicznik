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

    private static TestRestTemplate testRestTemplate;

    @Autowired
    public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
        AuthenticatedRequests.testRestTemplate = testRestTemplate;
    }

    public static ResponseEntity<String> getRequestAuth(String username, String password, String url) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .getForEntity(url, String.class);
    }

    public static ResponseEntity<String> getRequestAuth(String username, String password, String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return getRequestAuth(username, password, urlTemplate);
    }

    public static ResponseEntity<String> postRequestAuth(String username, String password, String url,
                                                         Object requestObject) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .postForEntity(url, requestObject, String.class);
    }

    public static ResponseEntity<String> postRequestAuth(String username, String password, String url,
                                                         Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return testRestTemplate
                .withBasicAuth(username, password)
                .postForEntity(urlTemplate, null, String.class);
    }

    public static ResponseEntity<String> postRequestAuth(String username, String password, String url,
                                                         Object request, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return testRestTemplate
                .withBasicAuth(username, password)
                .postForEntity(urlTemplate, request, String.class);
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password, String url, Object request) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.PUT, new HttpEntity(request), String.class);
    }

    public static ResponseEntity<String> putRequestAuth(String username, String password,
                                                        String url, Object request, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return putRequestAuth(username, password, urlTemplate, request);
    }

    public static ResponseEntity<String> patchRequestAuth(String username, String password,
                                                          String url, Object request) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.PATCH, new HttpEntity(request), String.class);
    }

    public static ResponseEntity<String> patchRequestAuth(String username, String password,
                                                          String url, Object request, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return patchRequestAuth(username, password, urlTemplate, request);
    }

    public static ResponseEntity<String> patchRequestAuth(String username, String password,
                                                          String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return patchRequestAuth(username, password, urlTemplate);
    }

    public static ResponseEntity<String> patchRequestAuth(String username, String password,
                                                          String url) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.PATCH, null, String.class);
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String
            password, String url) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
    }

    public static ResponseEntity<String> deleteRequestAuth(String username,
                                                           String password, String url, Map<String, ?> parameters) {
        String urlTemplate = buildURI(url, parameters);
        return deleteRequestAuth(username, password, urlTemplate);
    }

    public static ResponseEntity<String> deleteRequestAuth(String username, String
            password, String url, Object request) {
        return testRestTemplate
                .withBasicAuth(username, password)
                .exchange(url, HttpMethod.DELETE, new HttpEntity<>(request), String.class);
    }
}
