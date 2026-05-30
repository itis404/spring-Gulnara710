package ru.itis.ReadMe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuggestionController {

    @GetMapping("/suggest-book")
    public String suggestBookPage() {
        return "suggest-book";
    }
}
