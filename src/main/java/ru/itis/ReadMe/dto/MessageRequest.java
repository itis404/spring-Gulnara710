package ru.itis.ReadMe.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String content;
    private String replyToId;
}
