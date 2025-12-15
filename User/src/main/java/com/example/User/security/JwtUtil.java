package com.example.User.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    // 🔥 MISMO SECRET QUE AUTH (EXACTAMENTE IGUAL)
    private static final String SECRET_KEY =
            "MI_CLAVE_SECRETA_DE_32_CARACTERES_MINIMO_____123";

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24h

    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
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
        } catch (JwtException e) {
            System.out.println("JWT inválido: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // OBTENER CLAIMS
    // -------------------------------
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // -------------------------------
    // (Opcional) Generar token
    // -------------------------------
    public String generateToken(String email, Long userId, List<String> roles) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim("email", email)
                .claim("userId", userId)
                .claim("roles", roles)
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
