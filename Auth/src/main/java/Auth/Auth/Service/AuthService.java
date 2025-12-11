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

        // 1️⃣ Buscar usuario en el microservicio User
        UsuarioDTO user = userClient.getUserByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 2️⃣ Validar contraseña BCRYPT (antes estaba mal)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3️⃣ Generar token JWT con email + ID + roles
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                List.of(user.getRol())  // se envía como ["ADMIN"] o ["USER"]
        );

        // 4️⃣ Respuesta completa (tu clase LoginResponse debe aceptar esto)
        return new LoginResponse(token, user.getNombre(), List.of(user.getRol()));
    }
}
