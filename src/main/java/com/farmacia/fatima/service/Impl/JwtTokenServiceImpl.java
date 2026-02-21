package com.farmacia.fatima.service.Impl;

import com.farmacia.fatima.model.Usuario;
import com.farmacia.fatima.service.JwtTokenService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final Key key;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    public JwtTokenServiceImpl(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration}") Long accessTokenExpiration,
            @Value("${security.jwt.refresh-token-expiration}") Long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public String generarTokenAcceso(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(usuario.getEmail()) // usamos email como identificador de login
                .claim("rol", usuario.getRol().getNombre()) // un solo rol
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generarTokenRefresco(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String extraerUsuario(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 🔹 NUEVO MÉTODO para refrescar el accessToken
    public String generarTokenDesdeRefreshToken(String refreshToken) {
        if (!esTokenValido(refreshToken)) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        String email = extraerUsuario(refreshToken);

        Date ahora = new Date();
        Date nuevaExpiracion = new Date(ahora.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(ahora)
                .setExpiration(nuevaExpiracion)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
