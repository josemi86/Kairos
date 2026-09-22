package com.example.demo.service;

import com.example.demo.model.Show;
import com.example.demo.model.ShowComment;
import com.example.demo.model.http.CommentRequest;
import com.example.demo.repository.ShowCommentRepository;
import com.example.demo.repository.ShowRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShowService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowCommentRepository commentRepository;


    public List<Show> searchShows(String searchQuery) throws Exception {
        String url = "http://api.tvmaze.com/search/shows?q=" + searchQuery;
        List<Map<String, Object>> apiResponse = restTemplate.getForObject(url, List.class);
        List<Show> formattedShows = new ArrayList<>();
        if (apiResponse != null) {
            for (Map<String, Object> item : apiResponse) {
                Map<String, Object> showMap = (Map<String, Object>) item.get("show");
                if (showMap != null) {
                    Long id = ((Number) showMap.get("id")).longValue();
                    String name = (String) showMap.get("name");
                    String summary = (String) showMap.get("summary");
                    List<String> genres = (List<String>) showMap.get("genres");
                    String channel = "Unknown";
                    Map<String, Object> network = (Map<String, Object>) showMap.get("network");
                    Map<String, Object> webChannel = (Map<String, Object>) showMap.get("webChannel");

                    if (network != null && network.get("name") != null) {
                        channel = (String) network.get("name");
                    } else if (webChannel != null && webChannel.get("name") != null) {
                        channel = (String) webChannel.get("name");
                    }
                    formattedShows.add(new Show(id, name, channel, summary, genres, commentRepository.findByShowId(id)));
                }
            }
        }
        return formattedShows;
    }

    public Show getShowById(Long showId) throws HttpClientErrorException, Exception{
        Show response = showRepository.findById(Long.valueOf(showId));
        if(response!=null){
            response.setComments(commentRepository.findByShowId(showId));
            return response;
        }
        else {
            String url = "https://api.tvmaze.com/shows/" + showId;
            Map<String, Object> show = restTemplate.getForObject(url, Map.class);
            Long id = ((Number) show.get("id")).longValue();
            String name = (String) show.get("name");
            String summary = (String) show.get("summary");
            List<String> genres = (List<String>) show.get("genres");
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
            return showRecord;
        }
    }

    public void addComment(@Valid CommentRequest request) throws Exception {
        ShowComment newComment = new ShowComment(
                request.getShowId(),
                request.getComment(),
                request.getRating()
        );
        commentRepository.save(newComment);
    }
}
