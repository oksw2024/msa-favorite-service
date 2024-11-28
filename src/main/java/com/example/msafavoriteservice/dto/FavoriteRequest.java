package com.example.msafavoriteservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {
    private String bookIsbn;
    private String bookTitle;
    private String author;
    private String publisher;
    private String publicationYear;
    private String bookImageUrl;
}
