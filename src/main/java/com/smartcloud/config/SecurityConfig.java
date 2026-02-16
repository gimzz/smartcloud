package com.smartcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.smartcloud.security.JwtAuthenticationFilter;
import com.smartcloud.security.JwtTokenProvider;
import com.smartcloud.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

@Bean
SecurityFilterChain filterChain(
        HttpSecurity http,
        JwtTokenProvider tokenProvider,
        UserDetailsServiceImpl userDetailsService
) throws Exception {

    JwtAuthenticationFilter jwtFilter =
            new JwtAuthenticationFilter(tokenProvider, userDetailsService);

    http
        .csrf(csrf -> csrf.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/auth/**"
            ).permitAll()
            .anyRequest().authenticated()
        )

        .addFilterBefore(
            jwtFilter,
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
}


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
