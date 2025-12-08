package Auth.Auth.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
