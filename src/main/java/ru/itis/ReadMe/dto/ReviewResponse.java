package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private String content;
    private Integer rating;
    private String username;
    private LocalDateTime createdAt;
}
