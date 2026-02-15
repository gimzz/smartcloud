package com.smartcloud.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcloud.dto.UserCreateDto;
import com.smartcloud.dto.UserResponseDto;
import com.smartcloud.service.UserService;
import com.smartcloud.http.HttpResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDto input) {
        UserResponseDto created = userService.create(input);
        return HttpResponse.created(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        UserResponseDto user = userService.getById(id);
        return HttpResponse.ok(user);
    }
}
