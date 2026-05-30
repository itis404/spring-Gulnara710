package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itis.ReadMe.entity.BookSuggestionEntity;
import ru.itis.ReadMe.repository.BookSuggestionRepository;
import ru.itis.ReadMe.service.BookSuggestionService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequestMapping("/admin/suggestions")
@RequiredArgsConstructor
public class AdminSuggestionController {

    private final BookSuggestionRepository suggestionRepository;
    private final BookSuggestionService suggestionService;

    @GetMapping
    public String listSuggestions(Model model) {
        model.addAttribute("suggestions", suggestionService.getAllPending());
        return "admin/suggestions";
    }

    @GetMapping("/{id}/approve-redirect")
    public String approveRedirect(@PathVariable UUID id) {
        BookSuggestionEntity suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предложение не найдено"));
        String query = suggestion.getTitle() + " " + suggestion.getAuthor();

        return "redirect:/admin/books?suggestionId=" + id + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    @PostMapping("/{id}/reject")
    public String rejectSuggestion(@PathVariable UUID id,
                                   @RequestParam String comment,
                                   RedirectAttributes redirectAttributes) {
        suggestionService.rejectSuggestion(id, comment);
        redirectAttributes.addFlashAttribute("success", "Предложение отклонено");
        return "redirect:/admin/suggestions";
    }
}
