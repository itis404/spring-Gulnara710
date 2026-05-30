package ru.itis.ReadMe.dto;

import lombok.Data;

@Data
public class BookSuggestionRequest {
    private String title;
    private String author;
    private String comment;
}
