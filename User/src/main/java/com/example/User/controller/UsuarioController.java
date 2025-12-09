package com.example.User.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.User.model.Usuario;
import com.example.User.security.AuthenticatedUserPrincipal;
import com.example.User.service.UsuarioService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
@Tag(name = "Usuarios", description = "Operaciones CRUD para la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @GetMapping   //Enlista todo los usuarios unicamente para los admini
    @Operation(summary = "Listar usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }


    @GetMapping("/{id}")   // Obtener usuario por ID el admin o el mismo usuario
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<Usuario> obtener(
            @PathVariable Long id,
            Authentication auth
    ) {
        if (!puedeAcceder(id, auth)) {
            return ResponseEntity.status(403).build(); // FORBIDDEN
        }

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/email/{email}") //obtener email solo admin
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }


    @PostMapping // Registro
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con nombre, email, contraseña y rol."
    )
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.registrar(usuario);
        return ResponseEntity.ok(creado);
    }

    //Validacion de acceso
    private boolean puedeAcceder(Long idSolicitado, Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return true;

        Object principal = auth.getPrincipal();

        if (principal instanceof AuthenticatedUserPrincipal user) {
            Long userIdFromToken = Long.valueOf(user.getUserId());
            return userIdFromToken.equals(idSolicitado);
        }

        return false;
    }

    @GetMapping("/ping")
    public String pingUser() {
        return "User microservice OK";
    }
}
