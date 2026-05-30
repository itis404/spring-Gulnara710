package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private UUID replyToId;
    private String replyToUsername;
    private String replyToContent;
}
