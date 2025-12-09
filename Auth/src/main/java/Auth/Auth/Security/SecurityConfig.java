package Auth.Auth.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Es una API REST → desactivamos CSRF
            .csrf(csrf -> csrf.disable())

            // No queremos sesión de servidor, usamos JWT en otros microservicios
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                // 🔓 RUTAS PÚBLICAS EN AUTH
                .requestMatchers(
                    "/auth/login",          // login que genera el token
                    "/actuator/health",    // para que Railway vea que está vivo
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // 🔒 Cualquier otra cosa en Auth por ahora se bloquea
                .anyRequest().denyAll()
            );

        // No configuramos formLogin ni httpBasic porque Auth responde con JSON
        return http.build();
    }
}
