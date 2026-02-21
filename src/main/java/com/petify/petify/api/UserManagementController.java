package com.petify.petify.api;

import com.petify.petify.dto.ListingDTO;
import com.petify.petify.dto.UserDTO;
import com.petify.petify.service.AuthService;
import com.petify.petify.service.ListingService;
import com.petify.petify.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    // ...existing code...
    private final VerificationService verificationService;
    private final ListingService listingService;
    private final AuthService authService;

    public UserManagementController(AuthService authService, VerificationService verificationService, ListingService listingService) {
        this.authService = authService;
        this.verificationService = verificationService;
        this.listingService = listingService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("===== GET ALL USERS (Public) =====");
        try {
            List<UserDTO> users = authService.getAllUsers();
            logger.info("✓ Retrieved {} users", users.size());
            for (int i = 0; i < Math.min(users.size(), 3); i++) {
                UserDTO user = users.get(i);
                logger.info("User {}: ID={}, Type={}", i, user.getUserId(), user.getUserType());
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("❌ Error in getAllUsers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
     * Get all users (Admin only)
     * GET /api/users/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<UserDTO>> getAllUsersAdmin(@RequestHeader("X-User-Id") Long userId) {
        try {
            logger.info("========== GET ALL USERS (ADMIN) ==========");
            logger.info("Admin User ID: {}", userId);

            List<UserDTO> users = authService.getAllUsers();
            logger.info("✓ Retrieved {} users from AuthService", users.size());

            // Log first few users to verify userType is included
            for (int i = 0; i < Math.min(users.size(), 3); i++) {
                UserDTO user = users.get(i);
                logger.info("User {}: ID={}, Username={}, UserType={}",
                    i, user.getUserId(), user.getUsername(), user.getUserType());
            }

            logger.info("========== RETURNING {} USERS ==========", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("❌ Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * Get all listings (Admin only)
     * GET /api/users/admin/listings
     */
    @GetMapping("/admin/listings")
    public ResponseEntity<List<Map<String, Object>>> getAllListingsAdmin(@RequestHeader("X-User-Id") Long userId) {
        try {
            logger.info("========== GET ALL LISTINGS (ADMIN) ==========");
            logger.info("Admin User ID: {}", userId);

            List<ListingDTO> listings = listingService.getAllListings();
            logger.info("✓ Retrieved {} listings from ListingService", listings.size());

            // Enrich listings with owner names
            List<Map<String, Object>> enrichedListings = listings.stream()
                .map(listing -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("listingId", listing.getListingId());
                    map.put("animalId", listing.getAnimalId());
                    map.put("ownerId", listing.getOwnerId());
                    map.put("price", listing.getPrice());
                    map.put("status", listing.getStatus());
                    map.put("description", listing.getDescription());
                    map.put("createdAt", listing.getCreatedAt());

                    // Fetch owner name from users table
                    if (listing.getOwnerId() != null) {
                        try {
                            UserDTO owner = authService.getUserById(listing.getOwnerId());
                            map.put("ownerName", owner.getFirstName() + " " + owner.getLastName());
                            map.put("ownerUsername", owner.getUsername());
                            logger.debug("✓ Owner for listing {}: {}", listing.getListingId(), map.get("ownerName"));
                        } catch (Exception e) {
                            logger.warn("⚠ Could not fetch owner for listing {}: {}", listing.getListingId(), e.getMessage());
                            map.put("ownerName", "Unknown");
                            map.put("ownerUsername", "Unknown");
                        }
                    } else {
                        map.put("ownerName", "Unknown");
                        map.put("ownerUsername", "Unknown");
                    }

                    return map;
                })
                .collect(java.util.stream.Collectors.toList());

            logger.info("✓ Enriched {} listings with owner information", enrichedListings.size());
            logger.info("========== RETURNING {} LISTINGS ==========", enrichedListings.size());
            return ResponseEntity.ok(enrichedListings);
        } catch (Exception e) {
            logger.error("❌ Error fetching listings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
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
