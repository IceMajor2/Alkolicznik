package com.demo.alkolicznik.utils.request;

import com.demo.alkolicznik.security.filters.CookieAuthenticationFilter;
import com.vaadin.flow.server.VaadinRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtils {

    private static String IP_ADDRESS;
    private static String SCHEMA;

    @Autowired
    public void setIpAddress(String ipAddress) {
        CookieUtils.IP_ADDRESS = ipAddress;
    }

    @Autowired
    public void setSchema(String schema) {
        CookieUtils.SCHEMA = schema;
    }

    public static Cookie createTokenCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        if ("https".equals(SCHEMA))
            cookie.setSecure(true);
        else if ("http".equals(SCHEMA))
            cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("");
        return cookie;
    }

    private static BasicClientCookie createApacheTokenCookie(String token) {
        BasicClientCookie cookie = new BasicClientCookie("token", token);
        cookie.setHttpOnly(true);
        if ("https".equals(SCHEMA)) {
            cookie.setSecure(true);
        } else if ("http".equals(SCHEMA)) {
            cookie.setSecure(false);
        }
        cookie.setPath("/");
        cookie.setDomain(IP_ADDRESS);
        return cookie;
    }

    public static Cookie getAuthCookie(VaadinRequest currentRequest) {
        return Arrays.stream(currentRequest.getCookies())
                .filter(cookie ->
                        CookieAuthenticationFilter.JWT_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .orElse(null);
    }

    public static Cookie createExpiredTokenCookie(HttpServletRequest request) {
        if (findCookie(request, CookieAuthenticationFilter.JWT_COOKIE_NAME) == null) return null;
        Cookie cookie = new Cookie(CookieAuthenticationFilter.JWT_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    private static Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    protected static HttpContext getHttpContextWith(Cookie cookie) {
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

//    private static void printCookie(Cookie cookie) {
//        System.out.println("===");
//        System.out.println("Name: " + cookie.getName());
//        System.out.println("Value: " + cookie.getValue());
//        System.out.println("Domain: " + cookie.getDomain());
//        System.out.println("Path: " + cookie.getPath());
//        System.out.println("Max-Age: " + cookie.getMaxAge());
//        System.out.println("Secure: " + cookie.getSecure());
//        System.out.println("HttpOnly: " + cookie.isHttpOnly());
//        System.out.println("===");
//    }
}
