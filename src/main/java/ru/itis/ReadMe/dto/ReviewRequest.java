package ru.itis.ReadMe.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String content;
    private Integer rating;
}
