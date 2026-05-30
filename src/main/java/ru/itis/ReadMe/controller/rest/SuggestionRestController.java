package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.BookSuggestionRequest;
import ru.itis.ReadMe.dto.BookSuggestionResponse;
import ru.itis.ReadMe.service.BookSuggestionService;

import java.util.Map;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionRestController {

    private final BookSuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<?> createSuggestion(@RequestBody BookSuggestionRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            BookSuggestionResponse response = suggestionService.createSuggestion(userDetails.getUsername(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
