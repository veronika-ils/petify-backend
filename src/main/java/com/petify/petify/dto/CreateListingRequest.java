package com.petify.petify.dto;

import java.math.BigDecimal;

public class CreateListingRequest {
    private Long animalId;
    private String description;
    private BigDecimal price;

    // Constructors
    public CreateListingRequest() {}

    public CreateListingRequest(Long animalId, String description, BigDecimal price) {
        this.animalId = animalId;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
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
}
