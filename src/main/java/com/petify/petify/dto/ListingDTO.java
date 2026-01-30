package com.petify.petify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingDTO {
    private Long listingId;
    private Long ownerId;
    private Long animalId;
    private String description;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;

    // Constructors
    public ListingDTO() {}

    public ListingDTO(Long listingId, Long ownerId, Long animalId, String description, BigDecimal price, String status, LocalDateTime createdAt) {
        this.listingId = listingId;
        this.ownerId = ownerId;
        this.animalId = animalId;
        this.description = description;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
