package com.petify.petify.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "animals")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sex;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String species;

    private String breed;

    @Column(name = "located_name")
    private String locatedName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;


    // Constructors
    public Pet() {}

    public Pet(String name, String sex, LocalDate dateOfBirth, String photoUrl, String type, String species, String breed, String locatedName, Owner owner) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.photoUrl = photoUrl;
        this.type = type;
        this.species = species;
        this.breed = breed;
        this.locatedName = locatedName;
        this.owner = owner;
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

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

}



