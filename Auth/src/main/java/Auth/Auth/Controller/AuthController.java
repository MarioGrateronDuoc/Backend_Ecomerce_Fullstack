package Auth.Auth.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import Auth.Auth.Security.JwtUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceUrl = System.getenv().getOrDefault("USER_SERVICE_URL","http://localhost:8082");

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if(username==null || password==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","username and password required"));
        }
        try {
            String url = userServiceUrl + "/api/usuarios/email/" + username;
            UsuarioDto user = restTemplate.getForObject(url, UsuarioDto.class);
            if(user==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid credentials"));
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(!encoder.matches(password, user.getPassword())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid credentials"));
            }
            String token = JwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "authentication service error", "details", e.getMessage()));
        }
    }

    // simple DTO to map response from User service
    public static class UsuarioDto {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String role;

        public UsuarioDto(){}

        // getters and setters
        public Long getId(){return id;}
        public void setId(Long id){this.id=id;}
        public String getName(){return name;}
        public void setName(String name){this.name=name;}
        public String getEmail(){return email;}
        public void setEmail(String email){this.email=email;}
        public String getPassword(){return password;}
        public void setPassword(String password){this.password=password;}
        public String getRole(){return role;}
        public void setRole(String role){this.role=role;}
    }
}
