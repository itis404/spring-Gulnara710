package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.BookRelationRequest;
import ru.itis.ReadMe.dto.BookRelationResponse;
import ru.itis.ReadMe.service.UserBookRelationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRelationRestController {

    private final UserBookRelationService relationService;

    @PostMapping("/{bookId}/relation")
    public ResponseEntity<BookRelationResponse> setRelation(
            @PathVariable UUID bookId,
            @RequestBody BookRelationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookRelationResponse response = relationService.setStatus(bookId, request.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}/relation")
    public ResponseEntity<Map<String, String>> getUserBookStatus(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        var status = relationService.getUserBookStatus(bookId, userDetails.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("status", status.map(Enum::name).orElse(null));
        return ResponseEntity.ok(response);
    }
}
