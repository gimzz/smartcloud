package com.smartcloud.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartcloud.entity.Role;
import com.smartcloud.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("USER"));
                roleRepository.save(new Role("ADMIN"));
            }
        };
    }
}
