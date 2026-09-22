package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Shows")
public class Show {
    @Id
    private Long id;
    private String name;
    private String channel;
    private String summary;
    private List<String> genres;
    private List<ShowComment> comments;

    // Constructor
    public Show(Long id, String name, String channel, String summary, List<String> genres, List<ShowComment> comments) {
        this.id = id;
        this.name = name;
        this.channel = channel;
        this.summary = summary;
        this.genres = genres;
        this.comments = comments;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<ShowComment> getComments() {
        return comments;
    }

    public void setComments(List<ShowComment> comments) {
        this.comments = comments;
    }
}