package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.BookSuggestionRequest;
import ru.itis.ReadMe.dto.BookSuggestionResponse;
import ru.itis.ReadMe.entity.BookSuggestionEntity;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.BookSuggestionRepository;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSuggestionService {
    private final BookSuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final GoogleBooksService googleBooksService;

    @Transactional
    public BookSuggestionResponse createSuggestion(String username, BookSuggestionRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BookSuggestionEntity suggestion = BookSuggestionEntity.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .comment(request.getComment())
                .user(user)
                .status(BookSuggestionEntity.Status.PENDING)
                .build();
        suggestion = suggestionRepository.save(suggestion);
        log.info("New book suggestion from {}: {} by {}", username, request.getTitle(), request.getAuthor());
        return toResponse(suggestion);
    }

    public List<BookSuggestionResponse> getAllPending() {
        return suggestionRepository.findByStatusOrderByCreatedAtAsc(BookSuggestionEntity.Status.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public void rejectSuggestion(UUID suggestionId, String adminComment) {
        BookSuggestionEntity suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));
        suggestion.setStatus(BookSuggestionEntity.Status.REJECTED);
        suggestion.setAdminComment(adminComment);
        suggestionRepository.save(suggestion);
        log.info("Suggestion {} rejected", suggestionId);
    }

    private BookSuggestionResponse toResponse(BookSuggestionEntity s) {
        return new BookSuggestionResponse(
                s.getId(), s.getTitle(), s.getAuthor(), s.getComment(),
                s.getStatus().name(), s.getAdminComment(),
                s.getUser().getUsername(), s.getCreatedAt()
        );
    }

    @Transactional
    public void approveSuggestion(UUID suggestionId, String adminName) {
        log.info("approveSuggestion called for id={} by {}", suggestionId, adminName);
        BookSuggestionEntity suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new RuntimeException("Предложение не найдено"));
        if (suggestion.getStatus() != BookSuggestionEntity.Status.PENDING) {
            throw new RuntimeException("Предложение уже обработано");
        }
        suggestion.setStatus(BookSuggestionEntity.Status.APPROVED);
        suggestion.setAdminComment("Книга импортирована администратором " + adminName);
        suggestionRepository.save(suggestion);
        log.info("Suggestion {} approved", suggestionId);
    }
}
