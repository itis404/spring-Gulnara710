package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.itis.ReadMe.entity.UserBookRelation;
import ru.itis.ReadMe.service.UserBookRelationService;

@Controller
@RequiredArgsConstructor
public class MyBooksController {

    private final UserBookRelationService relationService;

    @GetMapping("/my-books")
    public String myBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        model.addAttribute("wishlist", relationService.getBooksByStatus(username, UserBookRelation.Status.WISHLIST));
        model.addAttribute("read", relationService.getBooksByStatus(username, UserBookRelation.Status.READ));
        return "my-books";
    }
}
