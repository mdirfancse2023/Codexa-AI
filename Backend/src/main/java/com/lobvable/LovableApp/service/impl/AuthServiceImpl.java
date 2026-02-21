package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.dto.auth.AuthResponse;
import com.lobvable.LovableApp.dto.auth.LoginRequest;
import com.lobvable.LovableApp.dto.auth.SignupRequest;
import com.lobvable.LovableApp.entity.User;
import com.lobvable.LovableApp.error.BadRequestException;
import com.lobvable.LovableApp.mapper.UserMapper;
import com.lobvable.LovableApp.repository.UserRepository;
import com.lobvable.LovableApp.security.AuthUtil;
import com.lobvable.LovableApp.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authehticationManager;

    //Moddelmapper vs mapstruct . Mapstruct is compile time mapping and faster than modelmapper which is runtime mapping.

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("Username already exists with username: "+request.username());
        });
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authehticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
