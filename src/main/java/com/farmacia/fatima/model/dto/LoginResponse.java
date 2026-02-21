package com.farmacia.fatima.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // en milisegundos
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
}
