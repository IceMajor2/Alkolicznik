package com.demo.alkolicznik.security;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	// TODO: Store it on a third-party service
	private static final String SECRET_KEY = "7253486947646342373574456a524d493077575331433850584446366b756552";

	public String extractUsername(String jwt) {
		return extractClaim(jwt, Claims::getSubject);
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(Collections.emptyMap(), userDetails);
	}

	public boolean isTokenValid(String jwt, UserDetails userDetails) {
		final String username = extractUsername(jwt);
		return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(jwt);
	}

	private boolean isTokenExpired(String jwt) {
		return extractExpiration(jwt).before(new Date());
	}

	private Date extractExpiration(String jwt) {
		return extractClaim(jwt, Claims::getExpiration);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				// expiration is 4 hours
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 4))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	private Claims extractAllClaims(String jwt) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(jwt)
				.getBody();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(jwt);
		return claimsResolver.apply(claims);
	}
}
