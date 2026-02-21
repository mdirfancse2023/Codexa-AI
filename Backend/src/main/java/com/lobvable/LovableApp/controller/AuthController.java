package com.lobvable.LovableApp.controller;

import com.lobvable.LovableApp.dto.auth.AuthResponse;
import com.lobvable.LovableApp.dto.auth.LoginRequest;
import com.lobvable.LovableApp.dto.auth.SignupRequest;
import com.lobvable.LovableApp.dto.auth.UserProfileResponse;
import com.lobvable.LovableApp.service.AuthService;
import com.lobvable.LovableApp.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {
    AuthService authService;
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(){
        Long userid = 1L; // TODO: get user id from security context
        return ResponseEntity.ok(userService.getProfile(userid));
    }
}
