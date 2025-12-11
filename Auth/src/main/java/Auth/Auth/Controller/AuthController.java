package Auth.Auth.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Auth.Auth.Security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String userServiceUrl =
            System.getenv().getOrDefault("USER_SERVICE_URL", "http://localhost:8081");

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email y contraseña requeridos."));
        }

        try {
            // 📌 Consultar microservicio USER
            String url = userServiceUrl + "/api/usuarios/email/" + email;

            UsuarioDto user = restTemplate.getForObject(url, UsuarioDto.class);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            // 📌 Validar contraseña BCRYPT
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            // 📌 Generar token
            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId(),
                    List.of(user.getRol())  // ["ADMIN"] o ["USER"]
            );

            // 📌 RESPUESTA COMPLETA PARA EL FRONTEND
            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "nombre", user.getNombre(),
                            "roles", List.of(user.getRol())
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error en autenticación",
                            "details", e.getMessage()
                    ));
        }
    }

    // DTO interno
    public static class UsuarioDto {
        private Long id;
        private String nombre;
        private String email;
        private String password;
        private String rol;

        public UsuarioDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }

    @GetMapping("/public/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Auth microservice OK");
    }
}
