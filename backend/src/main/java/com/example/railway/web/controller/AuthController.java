package com.example.railway.web.controller;

import com.example.railway.domain.UserAccount;
import com.example.railway.domain.enumeration.UserRole;
import com.example.railway.service.UserService;
import com.example.railway.web.dto.AuthResponse;
import com.example.railway.web.dto.LoginRequest;
import com.example.railway.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Simple mock login logic
        // In real app, verify password hash
        return userService.findByUsername(request.getUsername())
            .filter(user -> user.getPasswordHash().equals(request.getPassword())) // Plain text comparison for now!
            .map(user -> ResponseEntity.ok(AuthResponse.fromUser(user)))
            .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.existsByIdNumberOrPhone(request.getIdNumber(), request.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserAccount user = new UserAccount();
        user.setName(request.getName());
        user.setIdNumber(request.getIdNumber());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); // Plain text for now!
        user.setRole(UserRole.USER);

        UserAccount saved = userService.register(user);
        return ResponseEntity.ok(AuthResponse.fromUser(saved));
    }
}
