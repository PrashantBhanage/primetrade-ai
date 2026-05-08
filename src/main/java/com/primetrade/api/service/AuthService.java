// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/service/AuthService.java
package com.primetrade.api.service;

import com.primetrade.api.dto.JwtResponse;
import com.primetrade.api.dto.LoginRequest;
import com.primetrade.api.dto.RegisterRequest;
import com.primetrade.api.model.User;
import com.primetrade.api.repository.UserRepository;
import com.primetrade.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User saved = userRepository.save(user);
        // TODO: add email verification flow later if this goes to production
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getId(), saved.getRole().name());

        return toJwtResponse(saved, token);
    }

    public JwtResponse login(LoginRequest request) {
        // checking email manually here because
        // spring security exception messages are not great for API responses
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return toJwtResponse(user, token);
    }

    private JwtResponse toJwtResponse(User user, String token) {
        return JwtResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
