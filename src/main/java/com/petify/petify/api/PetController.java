package com.petify.petify.api;

import com.petify.petify.dto.AnimalResponseDTO;
import com.petify.petify.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /**
     * Get pet details by ID
     * GET /api/pets/{petId}
     * @param petId the pet/animal ID
     * @return the pet details as DTO
     */
    @GetMapping("/{petId}")
    public ResponseEntity<?> getPetById(@PathVariable Long petId) {
        try {
            logger.info("Fetching pet with ID: {}", petId);
            AnimalResponseDTO pet = petService.getPetById(petId);
            return ResponseEntity.ok(pet);
        } catch (RuntimeException e) {
            logger.error("Error fetching pet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching pet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve pet: " + e.getMessage()));
        }
    }
}
