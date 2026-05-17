package com.chatapp.controller;

import com.chatapp.dto.UserDTO;
import com.chatapp.entity.User;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {

    private final UserService userService;

    // NEW: list all users except the current one
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestHeader("X-User-Id") Long currentUserId) {
        try {
            List<UserDTO> users = userService.getAllUsersExcept(currentUserId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserDTOById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody User userDetails) {
        try {
            UserDTO updated = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status) {
        try {
            userService.updateUserStatus(id, status);
            return ResponseEntity.ok(new SuccessResponse("Status updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

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