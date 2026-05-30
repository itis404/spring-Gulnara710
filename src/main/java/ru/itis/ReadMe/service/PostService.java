package ru.itis.ReadMe.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import ru.itis.ReadMe.converter.PostConverter;
import ru.itis.ReadMe.dto.PostDto;
import ru.itis.ReadMe.entity.HashtagEntity;
import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.repository.HashtagRepository;
import ru.itis.ReadMe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostConverter postConverter;

    @Transactional
    @CacheEvict(value = {"posts", "postDtos"}, allEntries = true, beforeInvocation = true)
    public PostEntity createPost(PostEntity post, String content, String hashtagsInput) {
        post.setContent(content);
        post.setHashtags(parseHashtags(hashtagsInput));
        return postRepository.save(post);
    }

    private Set<HashtagEntity> parseHashtags(String input) {
        Set<HashtagEntity> hashtags = new HashSet<>();
        if (input == null || input.isBlank()) {
            return hashtags;
        }

        String[] tags = input.split(",");
        for (String tag : tags) {
            String cleanTag = tag.trim().replace("#", "").toLowerCase();
            if (cleanTag.isBlank()) continue;

            HashtagEntity hashtag = hashtagRepository.findByNameIgnoreCase(cleanTag)
                    .orElseGet(() -> {
                        HashtagEntity newTag = HashtagEntity.builder()
                                .name(cleanTag)
                                .build();
                        return hashtagRepository.save(newTag);
                    });

            hashtags.add(hashtag);
        }
        return hashtags;
    }

    @Cacheable(value = "posts")
    public List<PostEntity> getAllPosts() {
        return postRepository.findAllWithHashtagsAndUser();
    }

    @Cacheable(value = "postDtos")
    public List<PostDto> getAllPostDtos() {
        return getAllPosts().stream()
                .map(postConverter::toDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"posts", "postDtos"}, allEntries = true)
    @Transactional
    public PostEntity updatePost(PostEntity post, String hashtagsInput) {
        post.setHashtags(parseHashtags(hashtagsInput));
        return postRepository.save(post);
    }

    @CacheEvict(value = {"posts", "postDtos"}, allEntries = true)
    @Transactional
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

    public Optional<PostEntity> findById(UUID id) {
        return postRepository.findByIdWithHashtags(id);
    }
}