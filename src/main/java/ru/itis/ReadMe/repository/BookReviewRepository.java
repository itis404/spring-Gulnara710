package ru.itis.ReadMe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.ReadMe.entity.BookReview;
import java.util.UUID;

public interface BookReviewRepository extends JpaRepository<BookReview, UUID> {
    Page<BookReview> findByBookIdOrderByCreatedAtDesc(UUID bookId, Pageable pageable);
}
