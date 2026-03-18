package com.smartcloud.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider tokenProvider;
        private final UserDetailsServiceImpl userDetailsService;
        private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

        public JwtAuthenticationFilter(
                        JwtTokenProvider tokenProvider,
                        UserDetailsServiceImpl userDetailsService) {
                this.tokenProvider = tokenProvider;
                this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                        String token = header.substring(7);

                        if (tokenProvider.validateToken(token)) {
                                String username = tokenProvider.getUsernameFromToken(token);
                                log.debug("JWT validated for user={}", username);

                                var userDetails = userDetailsService.loadUserByUsername(username);

                                var authentication = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities());

                                authentication.setDetails(
                                                new WebAuthenticationDetailsSource().buildDetails(request));

                                SecurityContextHolder.getContext()
                                                .setAuthentication(authentication);
                        } else {
                                log.debug("Invalid JWT token");
                        }
                }

                filterChain.doFilter(request, response);
        }
}
