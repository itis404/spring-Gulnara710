package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.BookRelationResponse;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.entity.UserBookRelation;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.repository.UserBookRelationRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBookRelationService {

    private final UserBookRelationRepository relationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BookRelationResponse setStatus(UUID bookId, String statusName, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        UserBookRelation.Status status = statusName != null ? UserBookRelation.Status.valueOf(statusName) : null;

        var existing = relationRepository.findByUserIdAndBookId(user.getId(), book.getId());

        if (existing.isPresent()) {
            UserBookRelation relation = existing.get();
            if (status == null) {
                relationRepository.delete(relation);
                return new BookRelationResponse(null, "Статус сброшен");
            } else {
                relation.setStatus(status);
                relationRepository.save(relation);
                return new BookRelationResponse(status.name(), "Статус обновлён");
            }
        } else if (status != null) {
            UserBookRelation relation = UserBookRelation.builder()
                    .user(user)
                    .book(book)
                    .status(status)
                    .build();
            relationRepository.save(relation);
            return new BookRelationResponse(status.name(), "Статус установлен");
        }
        return new BookRelationResponse(null, "Без изменений");
    }

    public Optional<UserBookRelation.Status> getUserBookStatus(UUID bookId, String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> relationRepository.findByUserIdAndBookId(user.getId(), bookId))
                .map(UserBookRelation::getStatus);
    }

    public List<BookEntity> getBooksByStatus(String username, UserBookRelation.Status status) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return relationRepository.findByUserAndStatus(user, status)
                .stream()
                .map(UserBookRelation::getBook)
                .collect(Collectors.toList());
    }
}
