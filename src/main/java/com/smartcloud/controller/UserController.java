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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto dto) {
        return HttpResponse.created(userService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return HttpResponse.ok(userService.getById(id));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<Map<String, Object>> getByUsername(@PathVariable String username) {
        return HttpResponse.ok(userService.getByUsername(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<UserResponseDto> users = userService.getAll();
        return HttpResponse.ok(users);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Map<String, Object>> disable(@PathVariable Long id) {
        userService.disableById(id);
        return HttpResponse.noContent();
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Map<String, Object>> enable(@PathVariable Long id) {
        userService.enableById(id);
        return HttpResponse.noContent();
    }

}
