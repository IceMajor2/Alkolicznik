package com.demo.alkolicznik.utils.request;

import java.util.Map;

import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RequestUtils {

	private static RestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		RequestUtils.restTemplate = restTemplate;
	}

	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Map<String, ?> params, Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		String endpointWithParams = buildURI(endpoint, params);
		return restTemplate.exchange(endpointWithParams, method, getHttpEntityWith(cookie),
				responseClass, params);
	}

	private static HttpEntity getHttpEntityWith(Cookie cookie) {
		return getHttpEntityWith(null, cookie);
	}

	private static HttpEntity getHttpEntityWith(Object body, Cookie cookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie.getName() + "=" + cookie.getValue());
		return new HttpEntity(body, headers);
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
