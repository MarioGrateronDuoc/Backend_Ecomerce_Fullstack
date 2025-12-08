package Auth.Auth.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_STRING = "UnaClaveSecretaMuyLargaParaHS256QueDebeTenerAlMenos32Caracteres";
    
    // 2. Genera la Key a partir de la cadena secreta constante.
    private static final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public static String generateToken(String username) {
        Map<String,Object> claims = new HashMap<>();
        // Calcula la expiración: 24h
        long expirationTime = System.currentTimeMillis() + 1000L * 60 * 60 * 24; 
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationTime))
                // Usa la clave constante para firmar
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }
}