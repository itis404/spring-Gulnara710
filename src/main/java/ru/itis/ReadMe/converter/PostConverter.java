package ru.itis.ReadMe.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.ReadMe.dto.PostDto;
import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.entity.ReactionEntity;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostConverter {

    public PostDto toDto(PostEntity post) {
        if (post == null) return null;

        long likes = post.getReactions().stream()
                .filter(r -> r.getType() == ReactionEntity.ReactionType.LIKE)
                .count();
        long dislikes = post.getReactions().stream()
                .filter(r -> r.getType() == ReactionEntity.ReactionType.DISLIKE)
                .count();

        return PostDto.builder()
                .id(post.getId().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .username(post.getUser().getUsername())
                .hashtags(post.getHashtags().stream().map(h -> h.getName()).collect(Collectors.toSet()))
                .likesCount(likes)
                .dislikesCount(dislikes)
                .build();
    }
}
