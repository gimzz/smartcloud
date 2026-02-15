package com.smartcloud.dto;

import java.time.LocalDateTime;

import com.smartcloud.entity.User;

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponseDto() {}

    public UserResponseDto(Long id, String username, String email, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public static UserResponseDto fromEntity(User u) {
        if (u == null) return null;
        return new UserResponseDto(u.getId(), u.getUsername(), u.getEmail(), u.isEnabled(), u.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
