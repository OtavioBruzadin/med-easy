package com.example.med_easy.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.med_easy.auth.dto.AuthResponse;
import com.example.med_easy.auth.dto.LoginRequest;
import com.example.med_easy.auth.security.JwtService;
import com.example.med_easy.user.User;
import com.example.med_easy.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setup() {
        existingUser = new User(
                "Otavio",
                "otavio@email.com",
                "HASHED_PASSWORD",
                Instant.now()
        );
        existingUser.setId("user-123");
    }

    @Test
    void loginShouldReturnValidJwtToken() {
        String secret = "9f8c2a7e6b4d1c3a8e5f0d9b7a6c2e1f4a8b9c7d6e5f1a2b3c4d5e6f7a8b9c0d";
        JwtService realJwtService = new JwtService(secret, 3600L);

        AuthService realAuthService = new AuthService(
                userRepository,
                passwordEncoder,
                realJwtService
        );

        LoginRequest request = new LoginRequest("otavio@email.com", "Senha@123");

        when(userRepository.findByEmailIgnoreCase("otavio@email.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Senha@123", "HASHED_PASSWORD"))
                .thenReturn(true);

        AuthResponse response = realAuthService.login(request);

        assertNotNull(response.accessToken());
        assertTrue(response.accessToken().split("\\.").length == 3);
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(response.accessToken())
                .getPayload();

        assertEquals("otavio@email.com", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().toInstant().isAfter(Instant.now()));
    }

    @Test
    void loginShouldThrowBadCredentialsExceptionWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest("naoexiste@email.com", "Senha@123");

        when(userRepository.findByEmailIgnoreCase("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void loginShouldThrowBadCredentialsExceptionWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest("otavio@email.com", "SenhaErrada");

        when(userRepository.findByEmailIgnoreCase("otavio@email.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("SenhaErrada", "HASHED_PASSWORD"))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}