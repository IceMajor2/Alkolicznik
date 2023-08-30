package com.demo.alkolicznik.utils.requests;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class JWTRequests {

    private static TestRestTemplate testRestTemplate;

    @Autowired
    public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
        JWTRequests.testRestTemplate = testRestTemplate;
    }

    public static <T> ResponseEntity<String> getRequestJWT(String endpoint, Cookie cookie) {
        return testRestTemplate
                .exchange(endpoint, HttpMethod.GET, new HttpEntity<>(getCookieHeader(cookie)), String.class);
    }

    private static HttpHeaders getCookieHeader(Cookie cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=" + cookie.getValue());
        return headers;
    }
}
