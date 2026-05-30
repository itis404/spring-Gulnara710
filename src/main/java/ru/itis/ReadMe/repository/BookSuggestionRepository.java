package ru.itis.ReadMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.ReadMe.entity.BookSuggestionEntity;

import java.util.List;
import java.util.UUID;

public interface BookSuggestionRepository extends JpaRepository<BookSuggestionEntity, UUID> {
    List<BookSuggestionEntity> findByStatusOrderByCreatedAtAsc(BookSuggestionEntity.Status status);

    List<BookSuggestionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
