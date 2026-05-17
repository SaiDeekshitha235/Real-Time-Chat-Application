package com.chatapp.controller;

import com.chatapp.dto.LoginRequest;
import com.chatapp.dto.UserDTO;
import com.chatapp.dto.LoginResponse;
import com.chatapp.dto.SignupRequest;
import com.chatapp.entity.User;
import com.chatapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            UserDTO user = authService.signup(request);  // ← UserDTO now
            return ResponseEntity.ok(new SuccessResponse("User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // TODO: Extract userId from token and call authService.logout
            return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper DTOs
    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class SuccessResponse {
        private String message;
    }
}