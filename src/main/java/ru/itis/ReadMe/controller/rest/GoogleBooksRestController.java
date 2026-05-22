package ru.itis.ReadMe.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.service.GoogleBooksService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/books/google")
@RequiredArgsConstructor
public class GoogleBooksRestController {

    private final GoogleBooksService googleBooksService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        try {
            List<BookEntity> books = googleBooksService.searchBooks(q);
            return ResponseEntity.ok(books);
        } catch (IOException e) {
            log.error("Google Books API error: ", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importBook(@RequestBody BookEntity book) {
        try {
            BookEntity saved = googleBooksService.importBookToDatabase(book);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Import error: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to import book"));
        }
    }
}