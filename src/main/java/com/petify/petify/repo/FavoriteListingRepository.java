package com.petify.petify.repo;

import com.petify.petify.domain.Client;
import com.petify.petify.domain.FavoriteListing;
import com.petify.petify.domain.FavoriteListingId;
import com.petify.petify.domain.Listing;
import com.petify.petify.dto.ListingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface FavoriteListingRepository extends JpaRepository<FavoriteListing, FavoriteListingId> {
    List<FavoriteListing> findByClientUserId(Long clientId);
    Optional<FavoriteListing> findByClientAndListing(Client client, Listing listing);
    @Query("""
SELECT new com.petify.petify.dto.ListingDTO(
    l.listingId,
    l.owner.userId,
    l.animalId,
    l.description,
    l.price,
    l.status,
    l.createdAt
)
FROM FavoriteListing f
JOIN f.listing l
WHERE f.client.userId = :userId
ORDER BY l.createdAt DESC
""")
    List<ListingDTO> findFavoritedListingDTOs(@Param("userId") Long userId);
}
