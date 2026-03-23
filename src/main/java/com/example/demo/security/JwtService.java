package com.example.demo.security;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for JWT token generation, parsing, and validation.
 * Uses HMAC-SHA for signing with a secret key from application properties.
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration.access-token}")
    private long accessTokenExpiration;

    /**
     * Generates a signed JWT access token for the given user.
     *
     * @param extraClaims additional claims to embed in the token payload
     * @param user        the user to generate the token for
     * @return the compact JWT string
     */
    public String generateAccessToken(Map<String, Object> extraClaims, User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpiration)))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Extracts the subject (user ID) from a JWT token.
     *
     * @param token the JWT string
     * @return the subject claim (user UUID as string)
     */
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates whether a JWT token is structurally valid and not expired.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parses and verifies all claims from a JWT token.
     * Throws JwtException if the signature is invalid or the token is expired.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
