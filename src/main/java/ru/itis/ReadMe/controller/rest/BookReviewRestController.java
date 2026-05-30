package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.ReviewRequest;
import ru.itis.ReadMe.dto.ReviewResponse;
import ru.itis.ReadMe.service.BookReviewService;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookReviewRestController {

    private final BookReviewService reviewService;

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable UUID bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId, page, size));
    }

    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable UUID bookId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.addReview(
                bookId, request.getContent(), request.getRating(), userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
