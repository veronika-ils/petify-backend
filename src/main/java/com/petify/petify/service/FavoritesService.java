package com.petify.petify.service;

import com.petify.petify.domain.Client;
import com.petify.petify.domain.FavoriteListing;
import com.petify.petify.domain.Listing;
import com.petify.petify.dto.ListingDTO;
import com.petify.petify.repo.ClientRepository;
import com.petify.petify.repo.FavoriteListingRepository;
import com.petify.petify.repo.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private static final Logger logger = LoggerFactory.getLogger(FavoritesService.class);

    private final FavoriteListingRepository favoriteRepository;
    private final ClientRepository clientRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;

    public FavoritesService(FavoriteListingRepository favoriteRepository,
                           ClientRepository clientRepository,
                           ListingRepository listingRepository,
                           ListingService listingService) {
        this.favoriteRepository = favoriteRepository;
        this.clientRepository = clientRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
    }

    @Transactional
    public void addFavorite(Long userId, Long listingId) {
        Client client = clientRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found"));

        FavoriteListing favorite = new FavoriteListing(client, listing);
        favoriteRepository.save(favorite);
        logger.info("Added favorite - User: {}, Listing: {}", userId, listingId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long listingId) {
        Client client = clientRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new RuntimeException("Listing not found"));

        FavoriteListing favorite = favoriteRepository.findByClientAndListing(client, listing)
            .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.delete(favorite);
        logger.info("Removed favorite - User: {}, Listing: {}", userId, listingId);
    }

    @Transactional(readOnly = true)
    public List<ListingDTO> getFavoritedListings(Long userId) {
        return favoriteRepository.findFavoritedListingDTOs(userId);
    }


    public boolean isFavorited(Long userId, Long listingId) {
        Client client = clientRepository.findByUserId(userId)
            .orElse(null);

        if (client == null) return false;

        Listing listing = listingRepository.findById(listingId)
            .orElse(null);

        if (listing == null) return false;

        return favoriteRepository.findByClientAndListing(client, listing).isPresent();
    }

    private ListingDTO convertToDTO(Listing listing) {
        return new ListingDTO(
            listing.getListingId(),
            listing.getOwnerId(),
            listing.getAnimalId(),
            listing.getDescription(),
            listing.getPrice(),
            listing.getStatus(),
            listing.getCreatedAt()
        );
    }
}
