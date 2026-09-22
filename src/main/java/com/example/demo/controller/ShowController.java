package com.example.demo.controller;

import com.example.demo.model.ShowResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ShowController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/shows/search")
    public ResponseEntity<?> searchShows(@RequestParam(name = "search_query") String searchQuery) {
        // Validación básica
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'search_query' es requerido."));
        }

        String url = "http://api.tvmaze.com/search/shows?q=" + searchQuery;

        try {
            // Consumimos la API externa como una lista de mapas (JSON plano)
            List<Map<String, Object>> apiResponse = restTemplate.getForObject(url, List.class);
            List<ShowResponse> formattedShows = new ArrayList<>();

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
                        formattedShows.add(new ShowResponse(id, name, channel, summary, genres));
                    }
                }
            }

            return ResponseEntity.ok(formattedShows);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al conectar con el servicio externo."));
        }
    }
}
