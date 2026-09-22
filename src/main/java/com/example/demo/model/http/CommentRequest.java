package com.example.demo.model.http;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequest {
    @NotNull(message = "El show_id es requerido.")
    private Long showId;

    @NotBlank(message = "El comentario no puede estar vacío.")
    private String comment;

    @NotNull(message = "La calificación es requerida.")
    @Min(value = 0, message = "La calificación mínima es 0.")
    @Max(value = 5, message = "La calificación máxima es 5.")
    private Integer rating;

    // Getters y Setters
    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}