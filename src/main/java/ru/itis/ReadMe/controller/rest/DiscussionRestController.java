package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.MessageRequest;
import ru.itis.ReadMe.dto.MessageResponse;
import ru.itis.ReadMe.service.DiscussionService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionRestController {

    private final DiscussionService discussionService;

    @GetMapping("/join-by-book/{bookId}/status")
    public ResponseEntity<Map<String, Object>> getJoinStatus(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        UUID discussionId = discussionService.getDiscussionIdByBookId(bookId);
        boolean discussionExists = (discussionId != null);
        response.put("discussionExists", discussionExists);
        if (discussionExists && userDetails != null) {
            boolean isParticipant = discussionService.isParticipant(discussionId, userDetails.getUsername());
            response.put("isParticipant", isParticipant);
        } else {
            response.put("isParticipant", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join-by-book/{bookId}")
    public ResponseEntity<Void> joinDiscussionByBook(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        discussionService.joinDiscussionByBookId(bookId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{discussionId}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable UUID discussionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(discussionService.getMessageResponses(discussionId, page, size));
    }

    @PostMapping("/{discussionId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID discussionId,
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse response = discussionService.addMessage(
                discussionId,
                request.getContent(),
                userDetails.getUsername(),
                request.getReplyToId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{discussionId}/join")
    public ResponseEntity<Void> joinDiscussion(
            @PathVariable UUID discussionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        discussionService.joinDiscussion(discussionId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{discussionId}/leave")
    public ResponseEntity<Void> leaveDiscussion(
            @PathVariable UUID discussionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        discussionService.leaveDiscussion(discussionId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
