package com.petify.petify.api;

import com.petify.petify.dto.UserDTO;
import com.petify.petify.service.AuthService;
import com.petify.petify.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final AuthService authService;
    private final VerificationService verificationService;

    public UserManagementController(AuthService authService, VerificationService verificationService) {
        this.authService = authService;
        this.verificationService = verificationService;
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = authService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            UserDTO user = authService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get top 10 active users for verification
     * GET /api/users/verification/top-10
     */
    @GetMapping("/verification/top-10")
    public ResponseEntity<?> getTop10VerifiedUsers() {
        try {
            var topUsers = verificationService.getTop10ActiveUserIds();
            return ResponseEntity.ok(Map.of("topUsers", topUsers, "count", topUsers.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch top 10 users: " + e.getMessage()));
        }
    }

    /**
     * Check if user is verified (in top 10)
     * GET /api/users/{userId}/verified
     */
    @GetMapping("/{userId}/verified")
    public ResponseEntity<?> isUserVerified(@PathVariable Long userId) {
        try {
            boolean isVerified = verificationService.isUserVerified(userId);
            return ResponseEntity.ok(Map.of("userId", userId, "verified", isVerified));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check verification status: " + e.getMessage()));
        }
    }
}
