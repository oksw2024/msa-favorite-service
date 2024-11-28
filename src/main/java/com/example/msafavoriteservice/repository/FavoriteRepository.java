package com.example.msafavoriteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.msafavoriteservice.entity.Favorite;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // 사용자 ID와 ISBN으로 Favorite 찾기
    Optional<Favorite> findByUserIdAndBookIsbn(Long userId, String bookIsbn);
    // 특정 사용자 ID로 즐겨찾기 검색
    List<Favorite> findByUserId(Long userId);
}
