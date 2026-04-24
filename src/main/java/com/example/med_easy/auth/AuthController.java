package com.example.med_easy.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.med_easy.auth.dto.AuthResponse;
import com.example.med_easy.auth.dto.LoginRequest;
import com.example.med_easy.auth.dto.SignupRequest;
import com.example.med_easy.auth.dto.SignupResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}