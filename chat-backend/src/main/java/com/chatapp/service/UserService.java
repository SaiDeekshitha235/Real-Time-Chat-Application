package com.chatapp.service;

import com.chatapp.dto.UserDTO;
import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // NEW: returns all users except the current one, as DTOs
    public List<UserDTO> getAllUsersExcept(Long userId) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // EXISTING: kept for internal callers (e.g., AuthService) that need the entity
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // NEW: public-facing version that returns DTO (for the controller)
    public UserDTO getUserDTOById(Long id) {
        return UserDTO.fromEntity(getUserById(id));
    }

    // EXISTING: kept as-is, used internally
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // MODIFIED: returns UserDTO instead of User
    public UserDTO updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getBio() != null) {
            user.setBio(userDetails.getBio());
        }
        if (userDetails.getAvatarUrl() != null) {
            user.setAvatarUrl(userDetails.getAvatarUrl());
        }

        User saved = userRepository.save(user);
        return UserDTO.fromEntity(saved);
    }

    // CLEANED UP: same logic but uses ifPresent (matching the cleaner Optional pattern)
    public void updateUserStatus(Long userId, String status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            userRepository.save(user);
        });
    }
}