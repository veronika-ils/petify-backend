package com.petify.petify.service;

import com.petify.petify.domain.Owner;
import com.petify.petify.domain.Pet;
import com.petify.petify.domain.User;
import com.petify.petify.dto.AnimalResponseDTO;
import com.petify.petify.dto.CreatePetRequest;
import com.petify.petify.repo.OwnerRepository;
import com.petify.petify.repo.PetRepository;
import com.petify.petify.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

    public PetService(PetRepository petRepository, UserRepository userRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
    }

    /**
     * Add a pet for a user. If user is a CLIENT, promote them to OWNER.
     * @param userId the user ID
     * @param request the pet creation request
     * @return the created pet as DTO
     */
    @Transactional
    public AnimalResponseDTO addPet(Long userId,@RequestBody CreatePetRequest request) {

        // Validate required fields
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Pet name is required");
        }

        if (request.getSex() == null || request.getSex().isBlank()) {
            throw new RuntimeException("Pet sex is required");
        }

        if (request.getType() == null || request.getType().isBlank()) {
            throw new RuntimeException("Pet type is required");
        }


        if (request.getSpecies() == null || request.getSpecies().isBlank()) {
            throw new RuntimeException("Pet species is required");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(" User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });

        logger.info("Adding pet for user ID: {}", userId);

        // Check if user is already an owner, if not, promote them
        Owner owner = ownerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("⚠User {} is a CLIENT, promoting to OWNER", userId);
                    Owner newOwner = new Owner(user);
                    Owner savedOwner = ownerRepository.save(newOwner);
                    logger.info(" User promoted to OWNER with ID: {}", savedOwner.getUserId());
                    return savedOwner;
                });

        // Create new pet with all schema fields

        Pet pet = new Pet(
                request.getName(),
                request.getSex(),
                request.getDateOfBirth(),
                request.getPhotoUrl(),
                request.getType(),
                request.getSpecies(),
                request.getBreed(),
                request.getLocatedName(),
                owner
        );

        Pet savedPet = petRepository.save(pet);
        logger.info("Pet ID: {}, Owner ID: {}, Name: {}",
                savedPet.getAnimalId(), userId, savedPet.getName());

        AnimalResponseDTO result = new AnimalResponseDTO(savedPet);


        return result;
    }

    /**
     * Get pet details by ID
     * @param petId the pet/animal ID
     * @return the pet details as DTO
     */
    @Transactional(readOnly = true)
    public AnimalResponseDTO getPetById(Long petId) {
        logger.info("Fetching pet with ID: {}", petId);

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.error("Pet not found with ID: {}", petId);
                    return new RuntimeException("Pet not found");
                });

        logger.info("Pet found: {}", pet.getName());
        return new AnimalResponseDTO(pet);
    }
}
