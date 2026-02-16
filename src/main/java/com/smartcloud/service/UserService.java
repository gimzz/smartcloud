package com.smartcloud.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcloud.dto.UserCreateDto;
import com.smartcloud.dto.UserResponseDto;
import com.smartcloud.entity.Role;
import com.smartcloud.entity.User;
import com.smartcloud.exception.ConflictException;
import com.smartcloud.exception.NotFoundException;
import com.smartcloud.exception.MisconfiguredApplicationException;
import com.smartcloud.repository.RoleRepository;
import com.smartcloud.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
            .orElseThrow(() ->
                new MisconfiguredApplicationException(
                    "Default role 'USER' not found; please seed roles"
                )
            );

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = new User(
            username,
            email,
            encodedPassword,
            role
        );

        User saved = userRepository.save(user);

        return UserResponseDto.fromEntity(saved);
    }

    public UserResponseDto getById(Long id) {
        return UserResponseDto.fromEntity(getEntityById(id));
    }

    public UserResponseDto getByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return UserResponseDto.fromEntity(user);
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
            .stream()
            .map(UserResponseDto::fromEntity)
            .toList();
    }

    public void disableById(Long id) {
        User user = getEntityById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableById(Long id) {
        User user = getEntityById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    protected User getEntityById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
