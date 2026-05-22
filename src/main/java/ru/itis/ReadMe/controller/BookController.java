package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.service.BookService;

import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer minPages,
            @RequestParam(required = false) Integer maxPages,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        var pageable = PageRequest.of(page, size);
        var booksPage = bookService.findBooksWithFilters(title, author, genre, minPages, maxPages, pageable);

        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("genre", genre);
        model.addAttribute("minPages", minPages);
        model.addAttribute("maxPages", maxPages);
        return "books";
    }

    @GetMapping("/{id}")
    public String bookDetails(@PathVariable UUID id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        model.addAttribute("book", book);
        return "book-details";
    }
}