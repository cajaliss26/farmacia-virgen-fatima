package com.farmacia.fatima.service;

import com.farmacia.fatima.model.dto.LoginRequest;
import com.farmacia.fatima.model.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    String refrescarToken(String refreshToken);


}
