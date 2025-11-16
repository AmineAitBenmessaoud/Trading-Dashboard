package com.trading.backend.authservice.controller;

import com.trading.backend.authservice.dto.request.LoginRequest;
import com.trading.backend.authservice.dto.request.RegisterRequest;
import com.trading.backend.authservice.dto.response.AuthResponse;
import com.trading.backend.authservice.dto.response.UserInfo;
import com.trading.backend.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserInfo> register(@Valid @RequestBody RegisterRequest request) {
        UserInfo userInfo = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
}