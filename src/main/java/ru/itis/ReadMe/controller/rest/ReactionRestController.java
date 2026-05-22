package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.ReactionRequest;
import ru.itis.ReadMe.dto.ReactionResponse;
import ru.itis.ReadMe.service.ReactionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ReactionRestController {

    private final ReactionService reactionService;

    @PostMapping("/{postId}/reactions")
    public ResponseEntity<ReactionResponse> addReaction(
            @PathVariable String postId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReactionResponse response = reactionService.addReaction(
                UUID.fromString(postId),
                request.getType(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(response);
    }
}