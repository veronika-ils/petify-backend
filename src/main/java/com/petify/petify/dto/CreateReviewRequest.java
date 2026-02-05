package com.petify.petify.dto;

public class CreateReviewRequest {
    private Integer rating;
    private String comment;

    // Constructors
    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
