package com.example.msafavoriteservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {

    @JsonProperty("isbn13")
    private String isbn13;

    @JsonProperty("bookname")
    private String bookname;

    @JsonProperty("authors")
    private String authors;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("publication_year")
    private String publicationYear;

    @JsonProperty("bookImageURL")
    private String bookImageURL;

}
