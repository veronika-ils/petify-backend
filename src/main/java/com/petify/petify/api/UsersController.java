package com.petify.petify.api;

import com.petify.petify.domain.Pet;
import com.petify.petify.dto.AnimalResponseDTO;
import com.petify.petify.dto.CreatePetRequest;
import com.petify.petify.repo.PetRepository;
import com.petify.petify.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final PetRepository petRepository;
    private final PetService petService;

    public UsersController(PetRepository petRepository, PetService petService) {
        this.petRepository = petRepository;
        this.petService = petService;
    }

    /**
     * Get all pets for a specific owner/user
     * GET /api/users/{userId}/pets
     */
    @GetMapping("/{userId}/pets")
    public ResponseEntity<?> getUserPets(@PathVariable Long userId) {
        try {
            List<AnimalResponseDTO> pets = petRepository.findByOwnerUserId(userId)
                    .stream()
                    .map(AnimalResponseDTO::new)
                    .toList();

            return ResponseEntity.ok(pets);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to retrieve pets: " + e.getMessage()));
        }
    }

    /**
     * Add a new pet for a user (promotes CLIENT to OWNER if needed)
     * POST /api/users/{userId}/pets
     */
    @PostMapping("/{userId}/pets")
    public ResponseEntity<?> createPet(
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long headerUserId,
            @RequestBody CreatePetRequest request) {
        logger.info("========== CREATE PET ENDPOINT HIT ==========");
        logger.info("Path Variable userId: {}", userId);
        logger.info("Header X-User-Id: {}", headerUserId);
        logger.info("Request Body: {}", request);

        try {
            logger.info("Verifying user authorization...");
            // Verify the user is creating a pet for themselves
            if (!userId.equals(headerUserId)) {
                logger.error("❌ AUTHORIZATION FAILED: userId ({}) != headerUserId ({})", userId, headerUserId);
                return ResponseEntity.status(403)
                        .body(Map.of("error", "You can only create pets for yourself"));
            }
            logger.info("✅ Authorization passed");

            logger.info("Calling petService.addPet()...");
            AnimalResponseDTO pet = petService.addPet(userId, request);
            logger.info("✅ Pet created successfully");
            logger.info("Returning response...");
            return ResponseEntity.status(HttpStatus.CREATED).body(pet);
        } catch (RuntimeException e) {
            logger.error("❌ RuntimeException in createPet: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected Exception in createPet: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to create pet: " + e.getMessage()));
        }
    }

}