package com.example.User.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long.");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // -------------------------------
    // VALIDAR TOKEN
    // -------------------------------
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT inválido: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // OBTENER CLAIMS DEL TOKEN
    // -------------------------------
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // -------------------------------
    // GENERAR TOKEN (NO USADO EN USER, PERO QUEDA BIEN)
    // -------------------------------
    public String generateToken(String email, Long userId, List<String> roles) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim("email", email)
                .claim("userId", userId)       // 🔥 LONG, NO STRING
                .claim("roles", roles)         // ["ADMIN"] o ["USER"]
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
