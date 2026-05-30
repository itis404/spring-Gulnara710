package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private UUID id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private Integer pages;
    private Integer publishedYear;
    private String coverUrl;
}
