package com.example.User.controller;

import com.example.User.model.Usuario;
import com.example.User.security.AuthenticatedUserPrincipal;
import com.example.User.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
@Tag(name = "Usuarios", description = "Operaciones CRUD para la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping
    @Operation(summary = "Listar todos los usuarios (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }



    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID (ADMIN o dueño)")
    public ResponseEntity<Usuario> obtenerPorId(
            @PathVariable Long id,
            Authentication auth
    ) {
        if (!puedeAcceder(id, auth)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }



    @PostMapping
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario con nombre, email, contraseña y rol"
    )
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.registrar(usuario);
        return ResponseEntity.ok(creado);
    }



    @PutMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> cambiarRol(
            @PathVariable Long id,
            @RequestBody Usuario usuario
    ) {
        Usuario actualizado = usuarioService.cambiarRol(id, usuario.getRol());
        return ResponseEntity.ok(actualizado);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/public/ping")
    public String ping() {
        return "User microservice OK";
    }

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
}
