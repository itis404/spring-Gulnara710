package ru.itis.ReadMe.controller;

import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.service.PostService;
import ru.itis.ReadMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping
    public String posts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("post", new PostEntity());
        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute PostEntity post,
                             @RequestParam String content,
                             @RequestParam(required = false) String hashtagsInput,   // ← изменили имя
                             @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity author = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        post.setUser(author);
        postService.createPost(post, content, hashtagsInput);

        return "redirect:/posts";
    }

    // Форма редактирования поста
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        // Проверка, что текущий пользователь — автор поста
        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("У вас нет прав на редактирование этого поста");
        }

        model.addAttribute("post", post);
        return "posts/edit";
    }

    // Обновление поста
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable UUID id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) String hashtagsInput,
                             @AuthenticationPrincipal UserDetails userDetails) {
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("У вас нет прав на редактирование этого поста");
        }

        post.setTitle(title);
        post.setContent(content);
        postService.updatePost(post, hashtagsInput); // новый метод в сервисе
        return "redirect:/posts";
    }

    // Удаление поста
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable UUID id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("У вас нет прав на удаление этого поста");
        }

        postService.deletePost(id);
        return "redirect:/posts";
    }
}