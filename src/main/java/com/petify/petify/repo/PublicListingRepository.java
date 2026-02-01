package com.petify.petify.repo;
import com.petify.petify.domain.Listing;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PublicListingRepository extends JpaRepository<Listing, Long> {

    @Query("""
        SELECT new com.petify.petify.repo.PublicListingCardView(
          l.listingId,
          l.price,
          l.description,
          l.createdAt,
          l.animalId,
          l.owner.userId
        )
        FROM Listing l
        WHERE l.status = 'ACTIVE'
        ORDER BY l.createdAt DESC
        """)
    List<PublicListingCardView> findActiveListingCards();
}