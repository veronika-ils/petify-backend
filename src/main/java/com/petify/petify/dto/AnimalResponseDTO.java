package com.petify.petify.dto;

import com.petify.petify.domain.Pet;

public class AnimalResponseDTO {

    private Long animalId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String description;
    private Long ownerUserId;

    public AnimalResponseDTO(Pet animal) {
        this.animalId = animal.getAnimalId();
        this.name = animal.getName();
        this.species = animal.getSpecies();
        this.breed = animal.getBreed();
        this.age = animal.getAge();
        this.description = animal.getDescription();
        this.ownerUserId = animal.getOwner().getUser().getUserId();
    }
    // Getters and Setters

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}

