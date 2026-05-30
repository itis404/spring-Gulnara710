package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.entity.DiscussionEntity;
import ru.itis.ReadMe.service.BookService;
import ru.itis.ReadMe.service.DiscussionService;

import java.util.UUID;

@Controller
@RequestMapping("/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;
    private final BookService bookService;

    @GetMapping("/{bookId}")
    public String discussionPage(@PathVariable UUID bookId, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        DiscussionEntity discussion;
        try {
            discussion = discussionService.getByBookId(bookId);
        } catch (RuntimeException e) {
            return "redirect:/books/" + bookId + "?noDiscussion=true";
        }

        BookEntity book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        boolean isParticipant = userDetails != null &&
                discussionService.isParticipant(discussion.getId(), userDetails.getUsername());
        boolean isCreator = userDetails != null &&
                discussionService.isCreator(discussion.getId(), userDetails.getUsername());

        model.addAttribute("discussion", discussion);
        model.addAttribute("book", book);
        model.addAttribute("isParticipant", isParticipant);
        model.addAttribute("isCreator", isCreator);
        return "discussion";
    }
}
