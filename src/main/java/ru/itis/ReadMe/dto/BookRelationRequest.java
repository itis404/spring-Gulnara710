package ru.itis.ReadMe.dto;

import lombok.Data;

@Data
public class BookRelationRequest {
    private String status; // "WISHLIST", "READ", "FAVORITE" или null для удаления
}
