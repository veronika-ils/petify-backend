package com.petify.petify.api;

import com.petify.petify.dto.UserDTO;
import com.petify.petify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Get all admins
     * GET /api/admins
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        List<UserDTO> admins = authService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    /**
     * Get admin by ID
     * GET /api/admins/{adminId}
     */
    @GetMapping("/{adminId}")
    public ResponseEntity<UserDTO> getAdminById(@PathVariable Long adminId) {
        try {
            UserDTO admin = authService.getUserById(adminId);
            // Verify it's actually an admin
            if (!"ADMIN".equals(admin.getUserType().toString())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
