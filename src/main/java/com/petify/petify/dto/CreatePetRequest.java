package com.petify.petify.dto;

import java.time.LocalDate;

public class CreatePetRequest {
    private String name;
    private String sex;
    private LocalDate dateOfBirth;
    private String photoUrl;
    private String type;
    private String species;
    private String breed;
    private String locatedName;

    // Constructors
    public CreatePetRequest() {}

    public CreatePetRequest(String name, String sex, LocalDate dateOfBirth, String photoUrl,
                           String type, String species, String breed, String locatedName) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.photoUrl = photoUrl;
        this.type = type;
        this.species = species;
        this.breed = breed;
        this.locatedName = locatedName;
    }

    // Getters and Setters
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
}
