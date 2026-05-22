package ru.itis.ReadMe.dto;

import lombok.Data;

@Data
public class ReactionRequest {
    private String type; // "LIKE" или "DISLIKE"
}
