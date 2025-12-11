package Auth.Auth.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String nombre;
    private List<String> roles;

    // 🔹 Constructor completo usado por AuthService
    public LoginResponse(String token, String nombre, List<String> roles) {
        this.token = token;
        this.nombre = nombre;
        this.roles = roles;
    }

    // 🔹 Constructor antiguo (se mantiene por compatibilidad)
    public LoginResponse(String token) {
        this.token = token;
    }
}
