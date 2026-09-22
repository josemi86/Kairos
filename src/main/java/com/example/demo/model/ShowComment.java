package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Comments")
public class ShowComment {
    @Id
    private String id; // ID autogenerado por MongoDB

    @Field("show_id")
    private Long showId;

    private String comment;
    private Integer rating;

    public ShowComment() {}

    public ShowComment(Long showId, String comment, Integer rating) {
        this.showId = showId;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}

