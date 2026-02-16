package com.smartcloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcloud.entity.Role;
import com.smartcloud.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getByName(String name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new com.smartcloud.exception.MisconfiguredApplicationException("Role '" + name + "' not found; please seed roles."));
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }
    
    public Role createIfNotExists(String name) {
        return roleRepository.findByName(name)
            .orElseGet(() -> roleRepository.save(new Role(name)));
    }

}
