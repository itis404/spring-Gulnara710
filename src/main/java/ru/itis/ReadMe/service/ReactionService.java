package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.ReactionResponse;
import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.entity.ReactionEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.PostRepository;
import ru.itis.ReadMe.repository.ReactionRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReactionResponse addReaction(UUID postId, String type, String username) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ReactionEntity.ReactionType reactionType = ReactionEntity.ReactionType.valueOf(type);
        Optional<ReactionEntity> existing = reactionRepository.findByUserIdAndPostId(user.getId(), post.getId());

        if (existing.isPresent()) {
            ReactionEntity reaction = existing.get();
            if (reaction.getType() == reactionType) {
                // Если уже стоит такая же реакция – удаляем (снимаем)
                reactionRepository.delete(reaction);
            } else {
                // Иначе меняем тип
                reaction.setType(reactionType);
                reactionRepository.save(reaction);
            }
        } else {
            // Новой реакции – сохраняем
            ReactionEntity reaction = ReactionEntity.builder()
                    .type(reactionType)
                    .user(user)
                    .post(post)
                    .build();
            reactionRepository.save(reaction);
        }

        long likes = reactionRepository.countByPostIdAndType(postId, ReactionEntity.ReactionType.LIKE);
        long dislikes = reactionRepository.countByPostIdAndType(postId, ReactionEntity.ReactionType.DISLIKE);
        log.info("User {} {} post {}", username, type, postId);
        return new ReactionResponse(likes, dislikes);
    }
}
