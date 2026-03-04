package com.smartcloud.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

import com.smartcloud.dto.UserCreateDto;
import com.smartcloud.dto.UserResponseDto;
import com.smartcloud.http.HttpResponse;
import com.smartcloud.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Usuarios", description = "Gestión de usuarios en el sistema")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema con los datos proporcionados.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto dto) {
        return HttpResponse.created(userService.create(dto));
    }

    @Operation(summary = "Obtener usuario por ID", description = "Recupera los detalles de un usuario específico utilizando su ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return HttpResponse.ok(userService.getById(id));
    }

    @Operation(summary = "Obtener usuario por nombre de usuario", description = "Recupera los detalles de un usuario específico utilizando su nombre de usuario.")
    @GetMapping("/by-username/{username}")
    public ResponseEntity<Map<String, Object>> getByUsername(@PathVariable String username) {
        return HttpResponse.ok(userService.getByUsername(username));
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Recupera una lista de todos los usuarios registrados en el sistema. Solo accesible para administradores.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<UserResponseDto> users = userService.getAll();
        return HttpResponse.ok(users);
    }

    @Operation(summary = "Deshabilitar un usuario", description = "Deshabilita un usuario específico utilizando su ID. Solo accesible para administradores.")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Map<String, Object>> disable(@PathVariable Long id) {
        userService.disableById(id);
        return HttpResponse.noContent();
    }
    @Operation(summary = "Habilitar un usuario", description = "Habilita un usuario específico utilizando su ID. Solo accesible para administradores.")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Map<String, Object>> enable(@PathVariable Long id) {
        userService.enableById(id);
        return HttpResponse.noContent();
    }

}
