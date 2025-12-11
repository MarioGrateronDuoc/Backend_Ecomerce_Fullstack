package com.example.Productos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // ⚠️ IMPORTANTE: Debe ser exactamente igual al SECRET del microservicio Auth
    private final String SECRET = "MI_CLAVE_SECRETA_DE_32_CARACTERES_MINIMO_____123";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        logger.info("➡️ [FILTER] Endpoint recibido: {} {}", request.getMethod(), request.getRequestURI());

        String token = obtenerToken(request);

        if (token == null) {
            logger.warn("❌ No se encontró token en Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("🔑 Token recibido: {}", token);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            logger.info("✔️ Token validado correctamente");

            String email = claims.getSubject();
            List<String> roles = ((List<?>) claims.get("roles"))
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());

            logger.info("👤 Usuario del token (subject): {}", email);
            logger.info("🎭 Roles extraídos del token: {}", roles);

            List<GrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                    .collect(Collectors.toList());

            logger.info("🔐 Authorities generadas para Spring: {}", authorities);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            authorities
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.info("📌 SecurityContext actualizado. Usuario autenticado.");

        } catch (Exception e) {
            logger.error("❌ ERROR VALIDANDO TOKEN: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String obtenerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
