package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.CommentRequest;
import ru.itis.ReadMe.dto.CommentResponse;
import ru.itis.ReadMe.entity.CommentEntity;
import ru.itis.ReadMe.service.CommentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(UUID.fromString(postId)));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String postId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.addComment(
                UUID.fromString(postId),
                request.getContent(),
                userDetails.getUsername()
        ));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        CommentEntity comment = commentService.findById(commentId);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAuthor = comment.getUser().getUsername().equals(userDetails.getUsername());

        if (!isAuthor && !isAdmin) {
            return ResponseEntity.status(403).body(Map.of("error", "Нет прав на удаление"));
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
