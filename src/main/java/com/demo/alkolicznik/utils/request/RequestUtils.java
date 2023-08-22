package com.demo.alkolicznik.utils.request;

import java.util.Map;

import com.demo.alkolicznik.exceptions.ApiError;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
			Map<String, ?> params, Object body, Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		String endpointWithParams = buildURI(endpoint, params);
		return restTemplate.exchange(endpointWithParams, method, getHttpEntityWith(body, cookie),
				responseClass);
	}

	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		return request(method, endpoint, null, null, cookie, responseClass);
	}

	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Map<String, ?> params, Object body, Cookie cookie, Class<T> responseClass) {
		String endpointWithParams = buildURI(endpoint, params);
		return restTemplate.exchange(endpointWithParams, method, getHttpEntityWith(body, cookie),
				responseClass);
	}

	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Cookie cookie, Class<T> responseClass) {
		return request(method, endpoint, null, null, cookie, responseClass);
	}

	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Object body, Cookie cookie, Class<T> responseClass) {
		return request(method, endpoint, null, body, cookie, responseClass);
	}

	public static String extractErrorMessage(HttpClientErrorException e) {
		ApiError error = e.getResponseBodyAs(ApiError.class);
		return error.getMessage();
	}

	private static HttpEntity getHttpEntityWith(Object body, Cookie cookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", cookie.getName() + "=" + cookie.getValue());
		return new HttpEntity(body, headers);
	}

	private static String buildURI(String uriString, Map<String, ?> parameters) {
		if (parameters == null) return uriString;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
		for (var entry : parameters.entrySet()) {
			builder
					.queryParam(entry.getKey(), entry.getValue());
		}
		String urlTemplate = builder.encode().toUriString();
		return urlTemplate;
	}
}
