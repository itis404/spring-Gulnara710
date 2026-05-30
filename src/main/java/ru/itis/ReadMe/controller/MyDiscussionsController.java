package ru.itis.ReadMe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.ReadMe.entity.DiscussionEntity;
import ru.itis.ReadMe.service.DiscussionService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyDiscussionsController {

    private final DiscussionService discussionService;

    @GetMapping("/my-discussions")
    public String myDiscussions(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String username = userDetails.getUsername();
        log.info("User {} accessing my-discussions", username);
        Page<DiscussionEntity> discussions = discussionService.getDiscussionsByUser(username, page, size);
        model.addAttribute("discussions", discussions);
        return "my-discussions";
    }
}
