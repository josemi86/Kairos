package com.example.demo.controller;

import com.example.demo.model.CommentRequest;
import com.example.demo.model.Show;
import com.example.demo.model.ShowComment;
import com.example.demo.repository.ShowCommentRepository;
import com.example.demo.repository.ShowRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ShowController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowCommentRepository commentRepository;

    @GetMapping("/api/shows/search")
    public ResponseEntity<?> searchShows(@RequestParam(name = "search_query") String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'search_query' es requerido."));
        }
        String url = "http://api.tvmaze.com/search/shows?q=" + searchQuery;
        try {
            // Consumimos la API externa como una lista de mapas (JSON plano)
            List<Map<String, Object>> apiResponse = restTemplate.getForObject(url, List.class);
            List<Show> formattedShows = new ArrayList<>();

            if (apiResponse != null) {
                for (Map<String, Object> item : apiResponse) {
                    // TVMaze envuelve cada resultado en un objeto "show"
                    Map<String, Object> showMap = (Map<String, Object>) item.get("show");

                    if (showMap != null) {
                        Long id = ((Number) showMap.get("id")).longValue();
                        String name = (String) showMap.get("name");
                        String summary = (String) showMap.get("summary");
                        List<String> genres = (List<String>) showMap.get("genres");

                        // Lógica para extraer el canal (network o webChannel)
                        String channel = "Unknown";
                        Map<String, Object> network = (Map<String, Object>) showMap.get("network");
                        Map<String, Object> webChannel = (Map<String, Object>) showMap.get("webChannel");

                        if (network != null && network.get("name") != null) {
                            channel = (String) network.get("name");
                        } else if (webChannel != null && webChannel.get("name") != null) {
                            channel = (String) webChannel.get("name");
                        }

                        // Construimos nuestra respuesta limpia
                        formattedShows.add(new Show(id, name, channel, summary, genres, commentRepository.findByShowId(id)));
                    }
                }
            }

            return ResponseEntity.ok(formattedShows);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al conectar con el servicio externo."));
        }

    }

    @GetMapping("/api/shows/{show_id}")
    public ResponseEntity<?> getShowById(@PathVariable(name = "show_id") Long showId) {
        // Validación del ID
        if (showId == null || showId <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El ID del show debe ser un número entero positivo."));
        }

        Show response = showRepository.findById(Long.valueOf(showId));
        if(response!=null){
            response.setComments(commentRepository.findByShowId(showId));
            return ResponseEntity.ok(response);
        }
        else {
            String url = "https://api.tvmaze.com/shows/" + showId;

            try {
                // Consumimos el objeto completo como un Map genérico para retornar todo el JSON original

                Map<String, Object> show = restTemplate.getForObject(url, Map.class);
                Long id = ((Number) show.get("id")).longValue();
                String name = (String) show.get("name");
                String summary = (String) show.get("summary");
                List<String> genres = (List<String>) show.get("genres");

                // Lógica para extraer el canal (network o webChannel)
                String channel = "Unknown";
                Map<String, Object> network = (Map<String, Object>) show.get("network");
                Map<String, Object> webChannel = (Map<String, Object>) show.get("webChannel");

                if (network != null && network.get("name") != null) {
                    channel = (String) network.get("name");
                } else if (webChannel != null && webChannel.get("name") != null) {
                    channel = (String) webChannel.get("name");
                }
                Show showRecord = new Show(id, name, channel, summary, genres, commentRepository.findByShowId(id));
                showRepository.save(showRecord);
                return ResponseEntity.ok(showRecord);

            } catch (HttpClientErrorException.NotFound e) {
                // Manejo específico si el ID no existe en TVMaze (Retorna 404)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontró ningún show con el ID: " + showId));
            } catch (Exception e) {
                // Error genérico del servidor
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Error interno al conectar con el servicio externo."));
            }
        }
    }

    @PostMapping("/api/shows/comments")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request) {
        try {
            // Instanciar y mapear el documento
            ShowComment newComment = new ShowComment(
                    request.getShowId(),
                    request.getComment(),
                    request.getRating()
            );

            // Guardar en la base de datos de MongoDB
            commentRepository.save(newComment);

            // Retornar el estatus de la petición exitosa (201 Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Calificación y comentario guardados correctamente."
            ));

        } catch (Exception e) {
            // En caso de fallas con MongoDB
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "No se pudo procesar la solicitud en la base de datos."
            ));
        }
    }
}