package com.petify.petify.repo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PublicListingCardView {
    private Long listingId;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private Long animalId;
    private Long ownerId;

    public PublicListingCardView(Long listingId, BigDecimal price, String description,
                                  LocalDateTime createdAt, Long animalId, Long ownerId) {
        this.listingId = listingId;
        this.price = price;
        this.description = description;
        this.createdAt = createdAt;
        this.animalId = animalId;
        this.ownerId = ownerId;
    }

    public Long getListingId() {
        return listingId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public Long getOwnerId() {
        return ownerId;
    }
}

