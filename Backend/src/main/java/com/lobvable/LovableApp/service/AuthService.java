package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.auth.AuthResponse;
import com.lobvable.LovableApp.dto.auth.LoginRequest;
import com.lobvable.LovableApp.dto.auth.SignupRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
