package Auth.Auth.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MI_CLAVE_SECRETA_DE_32_CARACTERES_MINIMO_____123";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24h

    public String generateToken(String email, Long userId, List<String> roles) {

        // Convertir roles a formato Spring: "ADMIN" → "ROLE_ADMIN"
        List<String> authorities = roles.stream()
                .map(r -> "ROLE_" + r.toUpperCase())
                .toList();

        return Jwts.builder()
                .claim("email", email)
                .claim("userId", userId)
                .claim("roles", roles)  // opcional
                .claim("authorities", authorities) // ⭐ NECESARIO PARA SPRING
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), Jwts.SIG.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return (String) Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email");
    }
}
