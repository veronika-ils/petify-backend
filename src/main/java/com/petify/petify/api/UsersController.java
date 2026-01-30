package com.petify.petify.api;

import com.petify.petify.domain.Pet;
import com.petify.petify.repo.PetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final PetRepository petRepository;

    public UsersController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * Get all pets for a specific owner/user
     * GET /api/users/{userId}/pets
     */
    @GetMapping("/{userId}/pets")
    public ResponseEntity<?> getUserPets(@PathVariable Long userId) {
        try {
            List<Pet> pets = petRepository.findByOwnerId(userId);
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve pets: " + e.getMessage()));
        }
    }
}
