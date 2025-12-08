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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener header Authorization
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Validar token
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getClaims(token);

                String username = claims.getSubject();
                List<String> roles = claims.get("roles", List.class);
                String userId = claims.get("userId", String.class);

                // Convertir roles a SimpleGrantedAuthority
                List<SimpleGrantedAuthority> authorities =
                        roles.stream()
                             .map(SimpleGrantedAuthority::new)
                             .collect(Collectors.toList());

                // Crear un principal personalizado
                AuthenticatedUserPrincipal principal =
                        new AuthenticatedUserPrincipal(username, userId, roles);

                // Crear token de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                authorities
                        );

                // Guardarlo en el contexto
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
