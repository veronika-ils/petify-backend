package com.petify.petify.api;

import com.petify.petify.dto.UserDTO;
import com.petify.petify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final AuthService authService;

    public ClientController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Get all clients
     * GET /api/clients
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllClients() {
        List<UserDTO> clients = authService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    /**
     * Get client by ID
     * GET /api/clients/{clientId}
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<UserDTO> getClientById(@PathVariable Long clientId) {
        try {
            UserDTO client = authService.getUserById(clientId);
            // Verify it's actually a client
            if (!"CLIENT".equals(client.getUserType().toString())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
