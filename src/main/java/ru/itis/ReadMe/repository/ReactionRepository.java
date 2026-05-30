package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.ReactionEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, UUID> {

    Optional<ReactionEntity> findByUserIdAndPostId(UUID userId, UUID postId);

    @Query("SELECT COUNT(r) FROM ReactionEntity r WHERE r.post.id = :postId AND r.type = :type")
    long countByPostIdAndType(@Param("postId") UUID postId, @Param("type") ReactionEntity.ReactionType type);
}
