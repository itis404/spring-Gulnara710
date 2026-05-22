package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.PostEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    List<PostEntity> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT p FROM PostEntity p LEFT JOIN FETCH p.hashtags LEFT JOIN FETCH p.user ORDER BY p.createdAt DESC")
    List<PostEntity> findAllWithHashtagsAndUser();

    @Query("SELECT p FROM PostEntity p LEFT JOIN FETCH p.hashtags WHERE p.id = :id")
    Optional<PostEntity> findByIdWithHashtags(@Param("id") UUID id);
}
