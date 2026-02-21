package com.farmacia.fatima.service;

import com.farmacia.fatima.model.Usuario;

public interface JwtTokenService {
    String generarTokenAcceso(Usuario usuario);
    String generarTokenRefresco(Usuario usuario);
    boolean esTokenValido(String token);
    String extraerUsuario(String token);
    String generarTokenDesdeRefreshToken(String refreshToken);

}
