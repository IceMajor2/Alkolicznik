package com.demo.alkolicznik.utils.request;

import com.demo.alkolicznik.security.filters.CookieAuthenticationFilter;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtils {

	private static String ipAddress;

	@Autowired
	public void setIpAddress(String ipAddress) {
		CookieUtils.ipAddress = ipAddress;
	}

	public static Cookie createTokenCookie(String token) {
		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain("");
		return cookie;
	}

	private static BasicClientCookie createApacheTokenCookie(String token) {
		BasicClientCookie cookie = new BasicClientCookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain(ipAddress);
		return cookie;
	}

	public static Cookie getAuthCookie(VaadinRequest currentRequest) {
		return Arrays.stream(currentRequest.getCookies())
				.filter(cookie ->
						CookieAuthenticationFilter.JWT_COOKIE_NAME.equals(cookie.getName()))
				.findFirst()
				.orElse(null);
	}

	protected static HttpContext getHttpContextWith(Cookie cookie) {
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
