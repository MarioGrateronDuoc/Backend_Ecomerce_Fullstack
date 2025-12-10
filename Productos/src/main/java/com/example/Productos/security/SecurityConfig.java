package com.example.Productos.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        // No guardar sesiones → REST puro
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Reglas de autorización
        http.authorizeHttpRequests(auth -> auth

                // --- RUTAS PÚBLICAS (GET) ---
                .requestMatchers("GET", "/api/productos/**").permitAll()

                // --- RUTAS SOLO ADMIN (CRUD) ---
                .requestMatchers("POST", "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("PUT", "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/productos/**").hasRole("ADMIN")

                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
        );

        // Insertar el filtro JWT antes del filtro por defecto de Spring
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
