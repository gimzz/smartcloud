package com.smartcloud.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.smartcloud.dto.UserCreateDto;
import com.smartcloud.dto.UserResponseDto;
import com.smartcloud.entity.Role;
import com.smartcloud.entity.User;
import com.smartcloud.exception.ConflictException;
import com.smartcloud.exception.NotFoundException;
import com.smartcloud.repository.RoleRepository;
import com.smartcloud.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    
    public UserResponseDto create(UserCreateDto dto) {

        String username = dto.getUsername().trim().toLowerCase();
        String email = dto.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }

    Role role = roleRepository.findByName("USER")
        .orElseThrow(() -> new com.smartcloud.exception.MisconfiguredApplicationException("Default role 'USER' not found; please seed roles."));

        User user = new User(username, email, dto.getPassword(), role);

        User saved = userRepository.save(user);
        return UserResponseDto.fromEntity(saved);
    }

    public UserResponseDto getById(Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        return UserResponseDto.fromEntity(u);
    }
}
