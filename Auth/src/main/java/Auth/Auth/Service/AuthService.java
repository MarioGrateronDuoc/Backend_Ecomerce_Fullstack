package Auth.Auth.Service;

import Auth.Auth.Client.UserClient;
import Auth.Auth.model.LoginRequest;
import Auth.Auth.model.LoginResponse;
import Auth.Auth.model.UsuarioDTO;
import Auth.Auth.Security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserClient userClient;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserClient userClient, JwtUtil jwtUtil) {
        this.userClient = userClient;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {

        // 1. Buscar usuario en el microservicio User
        UsuarioDTO user = userClient.getUserByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 2. Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Generar token JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                List.of(user.getRol())
        );

        return new LoginResponse(token);
    }
}
