package com.example.med_easy.auth;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.med_easy.auth.dto.AuthResponse;
import com.example.med_easy.auth.dto.LoginRequest;
import com.example.med_easy.auth.dto.SignupRequest;
import com.example.med_easy.auth.dto.SignupResponse;
import com.example.med_easy.auth.security.JwtService;
import com.example.med_easy.user.User;
import com.example.med_easy.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        User user = new User(
                request.name().trim(),
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                Instant.now()
        );

        User saved = userRepository.save(user);

        return new SignupResponse(saved.getId(), saved.getName(), saved.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }
}