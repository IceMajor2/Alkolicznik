package com.demo.alkolicznik.utils.request;

import java.io.IOException;
import java.util.Map;

import com.demo.alkolicznik.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

	// TODO: to be removed completely
	private static RestTemplate restTemplate;

	// TODO: autowire this
	private static final String BASE_URL = "https://127.0.0.1:8433";

	private static ObjectMapper mapper;

	private static CloseableHttpClient httpClient;

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
		mapper.findAndRegisterModules();
		RequestUtils.mapper = mapper;
		return mapper;
	}

	@Autowired
	public void setHttpClient(CloseableHttpClient httpClient) {
		RequestUtils.httpClient = httpClient;
	}

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		RequestUtils.restTemplate = restTemplate;
	}

	public static <T> T patchRequest(String endpoint, Object requestBody, Cookie cookie,
			Class<T> classRef) throws ApiException {
		String jsonRequest = getAsJson(requestBody);
		HttpPatch httpPatch = new HttpPatch(BASE_URL + endpoint);
		httpPatch.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));
		HttpContext httpContext = getHttpContextWith(cookie);

		StringBuilder jsonResponse = new StringBuilder();
		try (CloseableHttpClient httpClient1 = RequestUtils.httpClient) {
			ClassicHttpResponse httpResponse = httpClient1.execute
					(httpPatch, httpContext, response -> {
						jsonResponse.append(EntityUtils.toString(response.getEntity()));
						return response;
					});
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		final String result = jsonResponse.toString();
		return parseToModel(result, classRef);
	}

	private static <T> T parseToModel(String json, Class<T> modelClass) {
		try {
			return mapper.readValue(json, modelClass);
		}
		catch (DatabindException e) {
			try {
				throw mapper.readValue(json, ApiException.class);
			}
			catch (JsonProcessingException ex) {
				throw new RuntimeException(ex);
			}
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpContext getHttpContextWith(Cookie cookie) {
		BasicCookieStore cookieStore = createCookieStoreWith(cookie);
		HttpContext localContext = HttpClientContext.create();
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}

	private static BasicCookieStore createCookieStoreWith(Cookie cookie) {
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie clientCookie = CookieUtils.createApacheTokenCookie(cookie.getValue());
		cookieStore.addCookie(clientCookie);
		return cookieStore;
	}

	private static String getAsJson(Object requestBody) {
		try {
			return mapper.writeValueAsString(requestBody);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}


	@Deprecated
	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Map<String, ?> params, Object body, Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		String endpointWithParams = buildURI(endpoint, params);
		return restTemplate.exchange(endpointWithParams, method, getHttpEntityWith(body, cookie),
				responseClass);
	}

	@Deprecated
	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		return request(method, endpoint, null, null, cookie, responseClass);
	}

	@Deprecated
	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Map<String, ?> params, Object body, Cookie cookie, Class<T> responseClass) {
		String endpointWithParams = buildURI(endpoint, params);
		ResponseEntity<T> object = restTemplate.exchange(endpointWithParams, method, getHttpEntityWith(body, cookie),
				responseClass);
		return object;
	}

	@Deprecated
	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Cookie cookie, Class<T> responseClass) {
		return request(method, endpoint, null, null, cookie, responseClass);
	}

	@Deprecated
	public static <T> ResponseEntity<T> request(HttpMethod method, String endpoint,
			Object body, Cookie cookie, Class<T> responseClass) {
		return request(method, endpoint, null, body, cookie, responseClass);
	}

	@Deprecated
	public static String extractErrorMessage(HttpClientErrorException e) {
		ApiException error = e.getResponseBodyAs(ApiException.class);
		return error.getMessage();
	}

	@Deprecated
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
