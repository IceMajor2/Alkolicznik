package com.demo.alkolicznik.gui.utils;

import com.demo.alkolicznik.dto.responses.BeerResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class ResponseUtils {

    private static String ROOT_URI = "http://localhost:8080";
    private static RestTemplate restTemplate;

    private ResponseUtils() {
    }

    @Autowired
    public void setRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .rootUri(ROOT_URI)
                .build();
    }

    public static ResponseEntity<List<BeerResponseDTO>> getAllBeersRequest() {
        var response = restTemplate
                .exchange("/api/admin/beer",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<BeerResponseDTO>>() {
                        });
        return response;
    }

    public static ResponseEntity<List<BeerResponseDTO>> getBeersRequest(String city) {
        String uri = buildURI("/api/beer", Map.of("city", city));
        var response = restTemplate.exchange(uri,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<BeerResponseDTO>>() {
                });
        return response;
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
