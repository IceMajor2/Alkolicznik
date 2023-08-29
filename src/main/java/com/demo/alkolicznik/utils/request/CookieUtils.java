package com.demo.alkolicznik.utils.request;

import com.demo.alkolicznik.security.filters.CookieAuthenticationFilter;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.Arrays;

public class CookieUtils {

	public static Cookie createTokenCookie(String token) {
		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain("");
		return cookie;
	}

	public static BasicClientCookie createApacheTokenCookie(String token) {
		BasicClientCookie cookie = new BasicClientCookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		// TODO: property
		cookie.setDomain("127.0.0.1");
		return cookie;
	}

	public static Cookie getAuthCookie(VaadinRequest currentRequest) {
		return Arrays.stream(currentRequest.getCookies())
				.filter(cookie ->
						CookieAuthenticationFilter.JWT_COOKIE_NAME.equals(cookie.getName()))
				.findFirst()
				.orElse(null);
	}

	public static HttpContext getHttpContextWith(Cookie cookie) {
		BasicCookieStore cookieStore = createCookieStoreWith(cookie);
		HttpContext localContext = HttpClientContext.create();
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		return localContext;
	}

	public static BasicCookieStore createCookieStoreWith(Cookie cookie) {
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie clientCookie = CookieUtils.createApacheTokenCookie(cookie.getValue());
		cookieStore.addCookie(clientCookie);
		return cookieStore;
	}
}
