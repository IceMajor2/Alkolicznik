package com.demo.alkolicznik.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static String jwtKey;

    @Autowired
    public void setJwtKey(Environment env) {
        JwtUtils.jwtKey = env.getProperty("jwt.key");
    }

    public static String generateToken(String username, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(jwtKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(String username, Date issuedAt, Date expiration, String signatureKey) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(signatureKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getSigningKey(String jwtKey) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
