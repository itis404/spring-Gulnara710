package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.itis.ReadMe.entity.BookSuggestionEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.repository.BookSuggestionRepository;
import ru.itis.ReadMe.repository.UserRepository;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MySuggestionsController {

    private final BookSuggestionRepository suggestionRepository;
    private final UserRepository userRepository;

    @GetMapping("/my-suggestions")
    public String mySuggestions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        log.info("Current username: {}", username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        List<BookSuggestionEntity> suggestions = suggestionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("suggestions", suggestions);
        return "my-suggestions";
    }
}
