package com.farmacia.fatima.controller;

import com.farmacia.fatima.model.dto.*;
import com.farmacia.fatima.model.Usuario;
import com.farmacia.fatima.service.AuthService;
import com.farmacia.fatima.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    // 🔹 Registro
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(request.getPassword());
        usuario.setTelefono(request.getTelefono());
        Usuario nuevo = usuarioService.registrarUsuario(usuario);

        RegisterResponse response = new RegisterResponse(
                "Usuario registrado correctamente",
                nuevo.getEmail()
        );
        return ResponseEntity.ok(response);
    }

    // 🔹 Login: devuelve tokens JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 🔹 Refresh: genera nuevos tokens si el anterior caducó
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String nuevoAccessToken = authService.refrescarToken(request.getRefreshToken());

            // ⚠️ En el refresh, no siempre tenemos todos los datos del usuario
            // Así que puedes devolver los campos adicionales como null o vacíos.
            LoginResponse response = new LoginResponse(
                    nuevoAccessToken,
                    request.getRefreshToken(),
                    900_000L,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new LoginResponse(null, null, 0L, null, null, null,null ,null )
            );
        }
    }


}
