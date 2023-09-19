package com.demo.alkolicznik.utils;

import com.demo.alkolicznik.utils.request.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class TestUtils {

    public static Cookie createTokenCookie(String token) {
        return CookieUtils.createTokenCookie(token);
    }

    public static String buildURI(String uriString, Map<String, ?> parameters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
        for (var entry : parameters.entrySet()) {
            builder
                    .queryParam(entry.getKey(), entry.getValue());
        }
        String urlTemplate = builder.encode().toUriString();
        return urlTemplate;
    }

    public static String removeTransformationFromURL(String url) {
        StringBuilder sb = new StringBuilder("https://");
        String[] pieces = url.substring(url.indexOf("ik.imagekit")).split("/");
        for (String piece : pieces) {
            if (piece.contains("tr:")) continue;
            sb.append(piece).append('/');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
