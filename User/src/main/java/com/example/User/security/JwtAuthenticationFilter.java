package com.example.User.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 🔴 Rutas que NO deben pasar por el filtro JWT (públicas)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Swagger, health, ping, etc. quedan libres
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator/health")
                || path.startsWith("/public")) {
            return true;
        }

        // Registro de usuario: solo el POST es público
        if (path.equals("/api/usuarios") && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        // Búsqueda por email (la usa el microservicio Auth para hacer login)
        if (path.startsWith("/api/usuarios/email")) {
            return true;
        }

        return false; // el resto SÍ pasa por el filtro (requiere JWT)
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 👇 Si no hay token, NO devolvemos 403 aquí, solo seguimos.
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // Validar token
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getClaims(token);

                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                String userId = claims.get("userId", String.class);

                List<SimpleGrantedAuthority> authorities =
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                AuthenticatedUserPrincipal principal =
                        new AuthenticatedUserPrincipal(username, userId, roles);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Token inválido → 401 (no 403)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
