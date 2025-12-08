package Auth.Auth.Client;

import Auth.Auth.model.UsuarioDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8081/api/usuarios";

    public UsuarioDTO getUserByEmail(String email) {
        String url = baseUrl + "/email/" + email;
        return restTemplate.getForObject(url, UsuarioDTO.class);
    }
}
