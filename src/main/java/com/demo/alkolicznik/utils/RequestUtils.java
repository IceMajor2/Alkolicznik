package com.demo.alkolicznik.utils;

import java.util.Arrays;
import java.util.Map;

import com.demo.alkolicznik.security.config.CookieAuthenticationFilter;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RequestUtils {

	private static WebClient webClient;

	@Autowired
	public void setWebClient(WebClient webClient) {
		RequestUtils.webClient = webClient;
	}

	public static Cookie createTokenCookie(String token) {
		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain("");
		return cookie;
	}

	public static Cookie getAuthCookie(VaadinRequest currentRequest) {
		return Arrays.stream(currentRequest.getCookies())
				.filter(cookie ->
						CookieAuthenticationFilter.JWT_COOKIE_NAME.equals(cookie.getName()))
				.findFirst()
				.orElse(null);
	}

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, String> parameters, Object body, Cookie cookie, Class<T> responseClass) {
		return webClient.method(method)
				.uri(uriBuilder -> {
					uriBuilder.path(endpoint);
					for (var entry : parameters.entrySet()) {
						uriBuilder.queryParam(entry.getKey(), entry.getValue());
					}
					return uriBuilder.build();
				})
				.cookie(cookie.getName(), cookie.getValue())
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(body)
				.retrieve()
				.bodyToMono(responseClass)
				.block();
	}

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, String> parameters, Cookie cookie, ParameterizedTypeReference<T> responseClass) {
		return webClient.method(method)
				.uri(uriBuilder -> {
					uriBuilder.path(endpoint);
					for (var entry : parameters.entrySet()) {
						uriBuilder.queryParam(entry.getKey(), entry.getValue());
					}
					return uriBuilder.build();
				})
				.cookie(cookie.getName(), cookie.getValue())
				.retrieve()
				.bodyToMono(responseClass)
				.block();
	}

	public static <T> T request(HttpMethod method, String endpoint,
			Map<String, String> parameters, Cookie cookie, Class<T> responseClass) {
		return webClient.method(method)
				.uri(uriBuilder -> {
					uriBuilder.path(endpoint);
					for (var entry : parameters.entrySet()) {
						uriBuilder.queryParam(entry.getKey(), entry.getValue());
					}
					return uriBuilder.build();
				})
				.cookie(cookie.getName(), cookie.getValue())
				.retrieve()
				.bodyToMono(responseClass)
				.block();
	}
}
