package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.ReadMe.dto.MessageResponse;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.entity.DiscussionEntity;
import ru.itis.ReadMe.entity.MessageEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.repository.DiscussionRepository;
import ru.itis.ReadMe.repository.MessageRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public DiscussionEntity createDiscussionForBook(BookEntity book, UserEntity admin) {
        if (discussionRepository.findByBookId(book.getId()).isPresent()) {
            throw new RuntimeException("Обсуждение для этой книги уже существует");
        }
        DiscussionEntity discussion = DiscussionEntity.builder()
                .name("Обсуждение книги: " + book.getTitle())
                .description("Обсуждаем книгу \"" + book.getTitle() + "\"")
                .book(book)
                .creator(admin)
                .build();
        discussion.getParticipants().add(admin);
        DiscussionEntity saved = discussionRepository.save(discussion);
        log.info("Создано обсуждение для книги {} администратором {}", book.getTitle(), admin.getUsername());
        return saved;
    }

    @Transactional
    public DiscussionEntity createDiscussionForBookIfNotExists(BookEntity book, String adminUsername) {
        return discussionRepository.findByBookId(book.getId())
                .orElseGet(() -> {
                    UserEntity admin = userRepository.findByUsername(adminUsername)
                            .orElseThrow(() -> new RuntimeException("Администратор не найден"));
                    return createDiscussionForBook(book, admin);
                });
    }

    @Transactional
    public DiscussionEntity getOrCreateDiscussionForBook(BookEntity book, String adminUsername) {
        return createDiscussionForBookIfNotExists(book, adminUsername);
    }

    public DiscussionEntity getByBookId(UUID bookId) {
        return discussionRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Обсуждение для этой книги не найдено"));
    }

    public Page<MessageEntity> getMessages(UUID discussionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByDiscussionIdOrderByCreatedAtAsc(discussionId, pageable);
    }

    @Transactional
    public MessageEntity addMessage(UUID discussionId, String content, String username, UUID replyToId) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Обсуждение не найдено"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (!discussion.getParticipants().contains(user)) {
            throw new RuntimeException("Вы не участник этого обсуждения");
        }
        MessageEntity replyTo = null;
        if (replyToId != null) {
            replyTo = messageRepository.findById(replyToId)
                    .orElseThrow(() -> new RuntimeException("Сообщение для ответа не найдено"));
        }
        MessageEntity message = MessageEntity.builder()
                .content(content)
                .user(user)
                .discussion(discussion)
                .replyTo(replyTo)
                .build();
        MessageEntity saved = messageRepository.save(message);
        log.info("Пользователь {} отправил сообщение в обсуждение {}", username, discussionId);
        return saved;
    }

    @Transactional
    public void joinDiscussion(UUID discussionId, String username) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Обсуждение не найдено"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (discussion.getParticipants().contains(user)) return;
        discussion.getParticipants().add(user);
        discussionRepository.save(discussion);
        log.info("Пользователь {} вступил в обсуждение {}", username, discussionId);
    }

    @Transactional
    public void leaveDiscussion(UUID discussionId, String username) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new RuntimeException("Обсуждение не найдено"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (discussion.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Создатель обсуждения не может выйти из него");
        }
        if (!discussion.getParticipants().contains(user)) {
            throw new RuntimeException("Вы не участник");
        }
        discussion.getParticipants().remove(user);
        discussionRepository.save(discussion);
        log.info("Пользователь {} покинул обсуждение {}", username, discussionId);
    }

    public boolean isParticipant(UUID discussionId, String username) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId).orElse(null);
        if (discussion == null) return false;
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;
        return discussion.getParticipants().contains(user);
    }

    public boolean isCreator(UUID discussionId, String username) {
        DiscussionEntity discussion = discussionRepository.findById(discussionId).orElse(null);
        if (discussion == null) return false;
        return discussion.getCreator().getUsername().equals(username);
    }

    public Page<DiscussionEntity> getDiscussionsByUser(String username, int page, int size) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(page, size);
        return discussionRepository.findByParticipantsContains(user, pageable);
    }

    public boolean isUserParticipantInBookDiscussion(UUID bookId, String username) {
        DiscussionEntity discussion = discussionRepository.findByBookId(bookId).orElse(null);
        if (discussion == null) return false;
        return isParticipant(discussion.getId(), username);
    }

    public UUID getDiscussionIdByBookId(UUID bookId) {
        return discussionRepository.findByBookId(bookId)
                .map(DiscussionEntity::getId)
                .orElse(null);
    }

    public Page<MessageResponse> getMessageResponses(UUID discussionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByDiscussionIdOrderByCreatedAtAsc(discussionId, pageable)
                .map(this::toMessageResponse);
    }

    @Transactional
    public MessageResponse addMessage(UUID discussionId, String content, String username, String replyToIdStr) {
        UUID replyToId = (replyToIdStr != null && !replyToIdStr.isBlank()) ? UUID.fromString(replyToIdStr) : null;
        MessageEntity saved = addMessage(discussionId, content, username, replyToId);
        return toMessageResponse(saved);
    }

    @Transactional
    public void joinDiscussionByBookId(UUID bookId, String username) {
        DiscussionEntity discussion = discussionRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Обсуждение для этой книги не найдено"));
        joinDiscussion(discussion.getId(), username);
    }

    private MessageResponse toMessageResponse(MessageEntity message) {
        MessageResponse resp = new MessageResponse();
        resp.setId(message.getId());
        resp.setContent(message.getContent());
        resp.setUsername(message.getUser().getUsername());
        resp.setCreatedAt(message.getCreatedAt());
        if (message.getReplyTo() != null) {
            resp.setReplyToId(message.getReplyTo().getId());
            resp.setReplyToUsername(message.getReplyTo().getUser().getUsername());
            resp.setReplyToContent(message.getReplyTo().getContent());
        }
        return resp;
    }
}
