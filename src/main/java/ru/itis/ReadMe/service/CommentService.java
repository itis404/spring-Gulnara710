package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.CommentResponse;
import ru.itis.ReadMe.entity.CommentEntity;
import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.CommentRepository;
import ru.itis.ReadMe.repository.PostRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> getCommentsByPost(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(UUID postId, String content, String username) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        CommentEntity comment = CommentEntity.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
        comment = commentRepository.save(comment);
        log.info("User {} commented on post {}", username, postId);
        return toResponse(comment);
    }

    private CommentResponse toResponse(CommentEntity comment) {
        return new CommentResponse(
                comment.getId().toString(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getCreatedAt()
        );
    }

    public CommentEntity findById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
    }

    @Transactional
    public void deleteComment(UUID id) {
        commentRepository.deleteById(id);
    }
}
