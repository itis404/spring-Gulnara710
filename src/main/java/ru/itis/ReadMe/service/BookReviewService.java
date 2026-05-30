package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.ReviewResponse;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.entity.BookReview;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.repository.BookReviewRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookReviewService {

    private final BookReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public Page<ReviewResponse> getReviewsByBook(UUID bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ReviewResponse addReview(UUID bookId, String content, Integer rating, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        BookReview review = BookReview.builder()
                .content(content)
                .rating(rating)
                .user(user)
                .book(book)
                .build();
        review = reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional
    public void deleteReview(UUID reviewId, String username) {
        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        if (!review.getUser().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет прав на удаление этого отзыва");
        }
        reviewRepository.delete(review);
    }

    private ReviewResponse toResponse(BookReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getUser().getUsername(),
                review.getCreatedAt()
        );
    }

    public Double getAverageRating(UUID bookId) {
        return reviewRepository.getAverageRatingByBookId(bookId);
    }
}
