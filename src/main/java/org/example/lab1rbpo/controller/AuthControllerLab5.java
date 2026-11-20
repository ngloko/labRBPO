package org.example.lab1rbpo.controller;

import jakarta.validation.Valid;
import org.example.lab1rbpo.dto.LoginRequest;
import org.example.lab1rbpo.dto.RefreshRequest;
import org.example.lab1rbpo.dto.RegisterRequestLab5;
import org.example.lab1rbpo.dto.TokenPairResponse;
import org.example.lab1rbpo.exception.InvalidCredentialsException;
import org.example.lab1rbpo.exception.InvalidRefreshTokenException;
import org.example.lab1rbpo.exception.UsernameAlreadyExistsException;
import org.example.lab1rbpo.service.AuthService;
import org.example.lab1rbpo.service.TokenPairService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthControllerLab5 {

    private final AuthService authService;
    private final TokenPairService tokenPairService;

    public AuthControllerLab5(AuthService authService, TokenPairService tokenPairService) {
        this.authService = authService;
        this.tokenPairService = tokenPairService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPairResponse> register(@Valid @RequestBody RegisterRequestLab5 request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(tokenPairService.refreshPair(request.refreshToken()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefresh(InvalidRefreshTokenException e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(UsernameAlreadyExistsException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", status.value(), "message", message));
    }
}