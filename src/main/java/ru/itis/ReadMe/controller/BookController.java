package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.entity.DiscussionEntity;
import ru.itis.ReadMe.service.BookReviewService;
import ru.itis.ReadMe.service.BookService;
import ru.itis.ReadMe.service.DiscussionService;

import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookReviewService bookReviewService;
    private final DiscussionService discussionService;

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
    public String bookDetails(@PathVariable UUID id, Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        Double avgRating = bookReviewService.getAverageRating(id);
        model.addAttribute("book", book);
        model.addAttribute("avgRating", avgRating);

        if (userDetails == null) {
            model.addAttribute("discussionExists", false);
            model.addAttribute("isParticipant", false);
            return "book-details";
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            DiscussionEntity discussion = discussionService.getByBookId(id);
            model.addAttribute("discussionExists", true);
            boolean isParticipant = discussionService.isParticipant(discussion.getId(), userDetails.getUsername());
            model.addAttribute("isParticipant", isParticipant);
        } catch (RuntimeException e) {

            if (isAdmin) {
                DiscussionEntity newDiscussion = discussionService.getOrCreateDiscussionForBook(book, userDetails.getUsername());
                model.addAttribute("discussionExists", true);
                model.addAttribute("isParticipant", true); // админ уже участник
            } else {
                model.addAttribute("discussionExists", false);
                model.addAttribute("isParticipant", false);
            }
        }
        return "book-details";
    }
}