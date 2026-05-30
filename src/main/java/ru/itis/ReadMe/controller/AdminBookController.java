package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    @GetMapping
    public String importPage(@RequestParam(value = "suggestionId", required = false) UUID suggestionId,
                             @RequestParam(value = "q", required = false) String query,
                             Model model) {
        model.addAttribute("suggestionId", suggestionId);
        model.addAttribute("q", query);
        return "admin/import-books";
    }
}
