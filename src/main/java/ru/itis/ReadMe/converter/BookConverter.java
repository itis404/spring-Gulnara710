package ru.itis.ReadMe.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.ReadMe.dto.BookDto;
import ru.itis.ReadMe.entity.BookEntity;

@Component
@RequiredArgsConstructor
public class BookConverter {

    public BookDto toDto(BookEntity entity) {
        if (entity == null) return null;
        return BookDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .description(entity.getDescription())
                .genre(entity.getGenre())
                .pages(entity.getPages())
                .publishedYear(entity.getPublishedYear())
                .coverUrl(entity.getCoverUrl())
                .build();
    }

    public BookEntity toEntity(BookDto dto) {
        if (dto == null) return null;
        return BookEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .description(dto.getDescription())
                .genre(dto.getGenre())
                .pages(dto.getPages())
                .publishedYear(dto.getPublishedYear())
                .coverUrl(dto.getCoverUrl())
                .build();
    }
}
