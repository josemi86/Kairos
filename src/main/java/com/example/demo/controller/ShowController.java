package com.example.demo.controller;

import com.example.demo.model.http.CommentRequest;
import com.example.demo.model.Show;
import com.example.demo.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping("/search")
    public ResponseEntity<?> searchShows(@RequestParam(name = "search_query") String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'search_query' es requerido."));
        }
        try {
        List<Show> shows = showService.searchShows(searchQuery);
        return ResponseEntity.ok(shows);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al conectar con el servicio externo."));
        }
    }

    @GetMapping("/{show_id}")
    public ResponseEntity<?> getShowById(@PathVariable(name = "show_id") Long showId) {
        if (showId == null || showId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El ID del show debe ser un número entero positivo."));
        }
        try {
            Show result = showService.getShowById(showId);
            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException.NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró ningún show con el ID: " + showId));
        } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error interno al conectar con el servicio externo."));
    }

    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request) {
        try {
            showService.addComment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Calificación y comentario guardados correctamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "No se pudo procesar la solicitud en la base de datos."
            ));
        }
    }
}