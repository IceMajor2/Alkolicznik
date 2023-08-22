package com.demo.alkolicznik.utils.request;

import java.util.Arrays;

import com.demo.alkolicznik.security.config.CookieAuthenticationFilter;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;

public class CookieUtils {

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
}
