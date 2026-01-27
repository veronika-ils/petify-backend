package com.petify.petify.api;

import com.petify.petify.dto.UserDTO;
import com.petify.petify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final AuthService authService;

    public OwnerController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Get all owners
     * GET /api/owners
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllOwners() {
        List<UserDTO> owners = authService.getAllOwners();
        return ResponseEntity.ok(owners);
    }

    /**
     * Get owner by ID
     * GET /api/owners/{ownerId}
     */
    @GetMapping("/{ownerId}")
    public ResponseEntity<UserDTO> getOwnerById(@PathVariable Long ownerId) {
        try {
            UserDTO owner = authService.getUserById(ownerId);
            // Verify it's actually an owner
            if (!"OWNER".equals(owner.getUserType().toString())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(owner);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
