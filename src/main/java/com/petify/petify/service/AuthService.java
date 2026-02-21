package com.petify.petify.service;

import com.petify.petify.domain.Client;
import com.petify.petify.domain.Owner;
import com.petify.petify.domain.User;
import com.petify.petify.domain.UserType;
import com.petify.petify.dto.AuthResponse;
import com.petify.petify.dto.LoginRequest;
import com.petify.petify.dto.SignUpRequest;
import com.petify.petify.dto.UserDTO;
import com.petify.petify.repo.AdminRepository;
import com.petify.petify.repo.ClientRepository;
import com.petify.petify.repo.OwnerRepository;
import com.petify.petify.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, ClientRepository clientRepository,
                      OwnerRepository ownerRepository, AdminRepository adminRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.ownerRepository = ownerRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user as CLIENT
     * All new users are registered as CLIENT by default
     * Creates both a User and a Client row in the database
     */
    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        // Check if username or email already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        logger.error(">>> SIGNUP METHOD HIT <<<");

        // Create new user as CLIENT
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName()
        );

        User savedUser = userRepository.save(user);
        logger.info("User saved successfully - ID: {}, Username: {}", savedUser.getUserId(), savedUser.getUsername());

        try {
            // Create a corresponding Client row
            Client client = new Client(savedUser);
            Client savedClient = clientRepository.save(client);
            logger.info("Client saved successfully - Client linked to User ID: {}", savedClient.getUser().getUserId());
        } catch (Exception e) {
            logger.error("Failed to create client for user ID: {}", savedUser.getUserId(), e);
            throw new RuntimeException("Failed to create client profile: " + e.getMessage(), e);
        }

        return new AuthResponse(
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            UserType.CLIENT
        );
    }

    /**
     * Login user with username and password
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());

        if (user.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User foundUser = user.get();

        // Debug logging to see foundUser details
        logger.info("User found - ID: {}, Username: {}, Email: {}, FirstName: {}, LastName: {}",
            foundUser.getUserId(),
            foundUser.getUsername(),
            foundUser.getEmail(),
            foundUser.getFirstName(),
            foundUser.getLastName());

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        logger.info("Password verified successfully for user: {}", foundUser.getUsername());

        // Determine user type by checking tables in order: ADMIN -> OWNER -> CLIENT
        UserType userType = UserType.CLIENT;

        if (adminRepository.existsById(foundUser.getUserId())) {
            userType = UserType.ADMIN;
            logger.info(" User is ADMIN");
        } else if (ownerRepository.existsById(foundUser.getUserId())) {
            userType = UserType.OWNER;
            logger.info(" User is OWNER");
        } else {
            logger.info(" User is CLIENT");
        }

        return new AuthResponse(
            foundUser.getUserId(),
            foundUser.getUsername(),
            foundUser.getEmail(),
            foundUser.getFirstName(),
            foundUser.getLastName(),
            userType
        );
    }


    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        logger.info("===== GET ALL USERS SERVICE =====");
        List<UserDTO> users = userRepository.findAll()
            .stream()
            .map(user -> {
                UserDTO dto = this.mapToDTO(user);
                logger.debug("✓ Mapped user {} with type: {}", user.getUserId(), dto.getUserType());
                return dto;
            })
            .collect(Collectors.toList());
        logger.info("✓ Successfully mapped {} users", users.size());
        return users;
    }




    /**
     * Map User entity to UserDTO
     */
    private UserDTO mapToDTO(User user) {
        // Determine user type by checking tables in order: ADMIN -> OWNER -> CLIENT
        String userType = "CLIENT";

        if (adminRepository.existsById(user.getUserId())) {
            userType = "ADMIN";
            logger.debug("✓ User {} is ADMIN", user.getUserId());
        } else if (ownerRepository.existsById(user.getUserId())) {
            userType = "OWNER";
            logger.debug("✓ User {} is OWNER", user.getUserId());
        } else {
            logger.debug("✓ User {} is CLIENT", user.getUserId());
        }

        UserDTO dto = new UserDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getCreatedAt(),
            userType
        );

        logger.debug("✓ Created UserDTO for {} with type: {}", user.getUsername(), userType);
        return dto;
    }
}
