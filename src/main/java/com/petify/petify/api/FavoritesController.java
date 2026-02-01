package com.petify.petify.api;

import com.petify.petify.dto.ListingDTO;
import com.petify.petify.service.FavoritesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @PostMapping("/{listingId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long listingId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            favoritesService.addFavorite(userId, listingId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Added to favorites"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long listingId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            favoritesService.removeFavorite(userId, listingId);
            return ResponseEntity.ok(Map.of("message", "Removed from favorites"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ListingDTO>> getFavoritedListings(
            @RequestHeader("X-User-Id") Long userId) {
        List<ListingDTO> favorites = favoritesService.getFavoritedListings(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{listingId}/is-favorited")
    public ResponseEntity<?> isFavorited(
            @PathVariable Long listingId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isFavorited = favoritesService.isFavorited(userId, listingId);
        return ResponseEntity.ok(Map.of("isFavorited", isFavorited));
    }
}
