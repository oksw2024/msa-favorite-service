package com.example.msafavoriteservice.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "bookIsbn"}))
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String bookIsbn;

    @Column(nullable = false)
    private String bookTitle;

    @Column
    private String author;

    @Column
    private String publisher;

    @Column
    private String publicationYear;

    @Column
    private String bookImageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
