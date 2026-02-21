package com.farmacia.fatima.service.Impl;

import com.farmacia.fatima.model.dto.LoginRequest;
import com.farmacia.fatima.model.dto.LoginResponse;
import com.farmacia.fatima.model.Usuario;
import com.farmacia.fatima.repository.UsuarioRepository;
import com.farmacia.fatima.service.AuthService;
import com.farmacia.fatima.service.JwtTokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepo;
    private final JwtTokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authManager,
                           UsuarioRepository usuarioRepo,
                           JwtTokenService tokenService,
                           PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.usuarioRepo = usuarioRepo;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Buscar usuario por email (ya no username)
        Usuario usuario = usuarioRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + request.getEmail()));

        // Verificar contraseña encriptada
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Autenticar (usando email)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Generar tokens JWT
        String accessToken = tokenService.generarTokenAcceso(usuario);
        String refreshToken = tokenService.generarTokenRefresco(usuario);

        // Tiempo de expiración: 15 minutos (900000 ms)
        Long expiresIn = 900_000L;

        // ✅ Devolver también los datos del usuario
        return new LoginResponse(
                accessToken,
                refreshToken,
                expiresIn,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().getNombre()
        );
    }

    @Override
    public String refrescarToken(String refreshToken) {
        // Validar el token de refresco
        if (!tokenService.esTokenValido(refreshToken)) {
            throw new RuntimeException("Token de refresco inválido o expirado");
        }

        // Generar un nuevo access token usando el refresh token
        return tokenService.generarTokenDesdeRefreshToken(refreshToken);
    }
}
