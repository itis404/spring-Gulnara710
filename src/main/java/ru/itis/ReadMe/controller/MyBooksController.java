package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.itis.ReadMe.entity.UserBookRelation;
import ru.itis.ReadMe.service.UserBookRelationService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyBooksController {

    private final UserBookRelationService relationService;

    @GetMapping("/my-books")
    public String myBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String username = userDetails.getUsername();
        log.info("User {} accessing my-books", username);
        model.addAttribute("wishlist", relationService.getBooksByStatus(username, UserBookRelation.Status.WISHLIST));
        model.addAttribute("read", relationService.getBooksByStatus(username, UserBookRelation.Status.READ));
        return "my-books";
    }
}
