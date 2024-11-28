package com.example.msafavoriteservice.service;

import com.example.msafavoriteservice.entity.Favorite;
import com.example.msafavoriteservice.repository.FavoriteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, RestTemplateBuilder restTemplateBuilder) {
        this.favoriteRepository = favoriteRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    // 사용자 ID를 기반으로 즐겨찾기 리스트 가져오기
    public List<Favorite> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    // 사용자 ID와 ISBN을 기반으로 즐겨찾기 삭제
    public void removeFavorite(Long userId, String bookIsbn) {
        Favorite favorite = favoriteRepository.findByUserIdAndBookIsbn(userId, bookIsbn)
                .orElseThrow(() -> new RuntimeException("Favorite not found for userId: " + userId + " and bookIsbn: " + bookIsbn));
        favoriteRepository.delete(favorite);
    }
    public List<Map<String, Object>> getRecommendations(String isbnList) {
        String authKey = "246bc9a1a2ea4ba78b5ada1b16a0ba7e43537ef40b0427f80013629f7b593a86";
        String apiUrl = "http://data4library.kr/api/recommandList?authKey=" + authKey + "&format=json&isbn13=" + isbnList;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode docsNode = rootNode.path("response").path("docs");

                List<Map<String, Object>> recommendations = new ArrayList<>();
                for (JsonNode doc : docsNode) {
                    JsonNode book = doc.path("book");
                    Map<String, Object> bookInfo = new HashMap<>();
                    bookInfo.put("no", book.path("no").asInt());
                    bookInfo.put("bookname", book.path("bookname").asText());
                    bookInfo.put("authors", book.path("authors").asText().replace("지은이: ", "").replace(";", " | "));
                    bookInfo.put("publisher", book.path("publisher").asText());
                    bookInfo.put("publication_year", book.path("publication_year").asText());
                    bookInfo.put("isbn13", book.path("isbn13").asText());
                    bookInfo.put("bookImageURL", book.path("bookImageURL").asText());

                    recommendations.add(bookInfo);
                }
                return recommendations;
            } else {
                throw new RuntimeException("Failed to fetch recommendations from external API");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching recommendations", e);
        }
    }
}

