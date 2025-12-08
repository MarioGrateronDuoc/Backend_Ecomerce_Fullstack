package Auth.Auth.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String rol;
}
