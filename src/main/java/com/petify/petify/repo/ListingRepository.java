package com.petify.petify.repo;

import com.petify.petify.domain.Listing;
import com.petify.petify.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByOwner(Owner owner);
    List<Listing> findByStatus(String status);
}
