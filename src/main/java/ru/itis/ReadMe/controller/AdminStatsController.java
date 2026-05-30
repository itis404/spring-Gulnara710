package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itis.ReadMe.service.HashtagService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final HashtagService hashtagService;

    @GetMapping("/popular-hashtags")
    public String popularHashtags(Model model) {
        model.addAttribute("hashtags", hashtagService.getPopularHashtagsAboveAverage());
        return "admin/popular-hashtags";
    }
}
