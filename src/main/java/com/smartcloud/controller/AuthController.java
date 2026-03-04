package com.smartcloud.controller;

import com.smartcloud.auth.LoginRequestDto;
import com.smartcloud.auth.LoginResponseDto;
import com.smartcloud.security.JwtTokenProvider;
import com.smartcloud.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Autenticación", description = "Gestión de autenticación en el sistema")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Iniciar sesión", description = "Permite a los usuarios iniciar sesión en el sistema y obtener un token JWT para autenticación en futuras solicitudes.")
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {

        var user = userService.getEntityByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());

        return new LoginResponseDto(token);
    }
}
