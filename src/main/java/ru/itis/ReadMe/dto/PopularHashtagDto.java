package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularHashtagDto {
    private String name;
    private Long postCount;
}
