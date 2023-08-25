package com.demo.alkolicznik.utils.request;

import java.io.IOException;
import java.util.Map;

import com.demo.alkolicznik.exceptions.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RequestUtils {

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

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, ?> parameters, Object requestBody, Cookie cookie,
			Class<T> classRef) throws ApiException {
		String jsonResponse = executeRequest(method, endpoint, parameters, requestBody, cookie);
		return parseToModel(jsonResponse, classRef);
	}

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, ?> parameters, Object requestBody, Cookie cookie,
			TypeReference<T> classRef) throws ApiException {
		String jsonResponse = executeRequest(method, endpoint, parameters, requestBody, cookie);
		return parseToModel(jsonResponse, classRef);
	}

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, ?> parameters, Cookie cookie,
			TypeReference<T> classRef) throws ApiException {
		return request(method, endpoint, parameters, null, cookie, classRef);
	}

	public static <T> T request(HttpMethod method, String endpoint, Cookie cookie,
			TypeReference<T> classRef) throws ApiException {
		return request(method, endpoint, null, null, cookie, classRef);
	}

	public static <T> T request(HttpMethod method, String endpoint, Cookie cookie,
			Class<T> classRef) throws ApiException {
		return request(method, endpoint, null, null, cookie, classRef);
	}

	public static <T> T request(HttpMethod method, String endpoint, Object requestBody,
			Cookie cookie, Class<T> classRef) throws ApiException {
		return request(method, endpoint, null, requestBody, cookie, classRef);
	}

	private static String executeRequest(HttpMethod method, String endpoint,
			Map<String, ?> parameters, Object requestBody, Cookie cookie) {
		String uri = buildURI(endpoint, parameters);
		String jsonRequest = getAsJson(requestBody);
		HttpUriRequestBase httpRequest = getHttpRequestObject(method, BASE_URL + uri);
		httpRequest.setEntity(new StringEntity(jsonRequest, ContentType.APPLICATION_JSON));
		HttpContext httpContext = CookieUtils.getHttpContextWith(cookie);

		StringBuilder jsonResponse = new StringBuilder();
		try (CloseableHttpClient httpClient1 = RequestUtils.httpClient) {
			httpClient1.execute(httpRequest, httpContext, response -> {
				jsonResponse.append(EntityUtils.toString(response.getEntity()));
				return response;
			});
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		final String result = jsonResponse.toString();
		return result;
	}

	private static HttpUriRequestBase getHttpRequestObject(HttpMethod method, String endpoint) {
		switch (method.name()) {
			case "GET" -> {
				return new HttpGet(endpoint);
			}
			case "POST" -> {
				return new HttpPost(endpoint);
			}
			case "PUT" -> {
				return new HttpPut(endpoint);
			}
			case "PATCH" -> {
				return new HttpPatch(endpoint);
			}
			case "DELETE" -> {
				return new HttpDelete(endpoint);
			}
		}
		throw new RuntimeException("The requested HttpMethod = '%s' is not supported"
				.formatted(method.name()));
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

	private static <T> T parseToModel(String json, TypeReference<T> modelClass) {
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

	private static String getAsJson(Object requestBody) {
		try {
			return mapper.writeValueAsString(requestBody);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
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
