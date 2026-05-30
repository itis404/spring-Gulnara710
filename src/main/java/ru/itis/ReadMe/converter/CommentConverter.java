package ru.itis.ReadMe.converter;

import org.springframework.stereotype.Component;
import ru.itis.ReadMe.dto.CommentDto;
import ru.itis.ReadMe.entity.CommentEntity;

@Component
public class CommentConverter {

    public CommentDto toDto(CommentEntity comment) {
        if (comment == null) return null;
        return CommentDto.builder()
                .id(comment.getId().toString())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
