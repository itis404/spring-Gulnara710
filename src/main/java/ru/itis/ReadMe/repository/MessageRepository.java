package ru.itis.ReadMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.ReadMe.entity.MessageEntity;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByDiscussionIdOrderByCreatedAtAsc(UUID discussionId, Pageable pageable);
}
