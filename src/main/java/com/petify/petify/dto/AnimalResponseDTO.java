package com.petify.petify.dto;

import com.petify.petify.domain.Pet;
import java.time.LocalDate;

public class AnimalResponseDTO {

    private Long animalId;
    private String name;
    private String sex;
    private LocalDate dateOfBirth;
    private String photoUrl;
    private String type;
    private String species;
    private String breed;
    private String locatedName;
    private Long ownerUserId;

    public AnimalResponseDTO(Pet animal) {
        this.animalId = animal.getAnimalId();
        this.name = animal.getName();
        this.sex = animal.getSex();
        this.dateOfBirth = animal.getDateOfBirth();
        this.photoUrl = animal.getPhotoUrl();
        this.type = animal.getType();
        this.species = animal.getSpecies();
        this.breed = animal.getBreed();
        this.locatedName = animal.getLocatedName();
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLocatedName() {
        return locatedName;
    }

    public void setLocatedName(String locatedName) {
        this.locatedName = locatedName;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}

