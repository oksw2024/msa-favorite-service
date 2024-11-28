package com.example.msafavoriteservice.controller;

import com.example.msafavoriteservice.dto.FavoriteRequest;
import com.example.msafavoriteservice.entity.Favorite;
import com.example.msafavoriteservice.repository.FavoriteRepository;
import com.example.msafavoriteservice.service.FavoriteService;
import com.example.msafavoriteservice.service.TokenService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteRepository favoriteRepository, FavoriteService favoriteService, RestTemplateBuilder restTemplateBuilder) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteService = favoriteService;
    }

    @Autowired
    private TokenService tokenService;

    // 즐겨찾기 조회
    @GetMapping("/get")
    public ResponseEntity<List<Favorite>> getFavorites(@RequestHeader("Authorization") String accessToken) {
        Long userId = tokenService.extractUserId(accessToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Fetching favorites for userId: {}", userId);
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }

    // 즐겨찾기 추가
    @PostMapping("/add")
    public ResponseEntity<String> addFavorite(@RequestHeader("Authorization") String accessToken,
                                              @RequestBody FavoriteRequest request) {
        Long userId = tokenService.extractUserId(accessToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookIsbn(request.getBookIsbn());
        favorite.setBookTitle(request.getBookTitle());
        favorite.setAuthor(request.getAuthor());
        favorite.setPublisher(request.getPublisher());
        favorite.setPublicationYear(request.getPublicationYear());
        favorite.setBookImageUrl(request.getBookImageUrl());
        favorite.setCreatedAt(LocalDateTime.now());

        log.info("Saving favorite: {}", favorite);
        favoriteRepository.save(favorite);
        return ResponseEntity.ok("Favorite added successfully");
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFavorite(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody FavoriteRequest request
    ) {
        Long userId = tokenService.extractUserId(accessToken);
        if (userId == null) {
            throw new RuntimeException("userId not found.");
        }

        log.info("Removing favorite for userId: {}", userId);
        favoriteService.removeFavorite(userId, request.getBookIsbn());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestHeader("Authorization") String accessToken) {
        Long userId = tokenService.extractUserId(accessToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        if (favorites.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No favorite books found");
        }

        String isbnList = favorites.stream()
                .map(Favorite::getBookIsbn)
                .collect(Collectors.joining(";"));

        log.info("ISBN list for recommendations: {}", isbnList);
        if (isbnList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No ISBNs to recommend");
        }

        try {
            List<Map<String, Object>> recommendations = favoriteService.getRecommendations(isbnList);
            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            log.error("Error fetching recommendations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching recommendations");
        }
    }
}
