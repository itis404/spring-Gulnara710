package ru.itis.ReadMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.ReadMe.entity.DiscussionEntity;
import ru.itis.ReadMe.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface DiscussionRepository extends JpaRepository<DiscussionEntity, UUID> {
    Optional<DiscussionEntity> findByBookId(UUID bookId);
    Page<DiscussionEntity> findByParticipantsContains(UserEntity user, Pageable pageable);
}
