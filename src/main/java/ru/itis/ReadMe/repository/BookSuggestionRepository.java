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

    Page<BookSuggestionEntity> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT u.username, COUNT(s) FROM BookSuggestionEntity s JOIN s.user u GROUP BY u HAVING COUNT(s) > 3")
    List<Object[]> findActiveSuggesters();

    List<BookSuggestionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
