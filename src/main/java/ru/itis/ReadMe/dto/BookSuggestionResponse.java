package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSuggestionResponse {
    private UUID id;
    private String title;
    private String author;
    private String comment;
    private String status;
    private String adminComment;
    private String username;
    private LocalDateTime createdAt;
}
