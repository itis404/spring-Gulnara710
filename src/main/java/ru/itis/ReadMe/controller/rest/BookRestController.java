package ru.itis.ReadMe.controller.rest;

import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookRepository bookRepository;

    @GetMapping
    public ResponseEntity<List<BookEntity>> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minPages,
            @RequestParam(required = false) Integer maxPages) {

        var spec = BookSpecification.withFilters(title, author, genre, minPages, maxPages);
        List<BookEntity> books = bookRepository.findAll(spec);

        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookEntity> getBook(@PathVariable UUID id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
