package ru.itis.ReadMe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itis.ReadMe.dto.PostForm;
import ru.itis.ReadMe.entity.PostEntity;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.service.PostService;
import ru.itis.ReadMe.service.UserService;

import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping
    public String posts(Model model) {
        model.addAttribute("posts", postService.getAllPostDtos());
        return "posts";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "posts/create";
    }

    @PostMapping
    public String createPost(@Valid @ModelAttribute("postForm") PostForm postForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "posts/create";
        }
        UserEntity author = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        PostEntity post = new PostEntity();
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setUser(author);
        postService.createPost(post, postForm.getContent(), postForm.getHashtagsInput());
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("У вас нет прав на редактирование этого поста");
        }

        PostForm postForm = new PostForm();
        postForm.setTitle(post.getTitle());
        postForm.setContent(post.getContent());
        String hashtags = post.getHashtags().stream()
                .map(h -> h.getName())
                .collect(Collectors.joining(", "));
        postForm.setHashtagsInput(hashtags);
        model.addAttribute("postForm", postForm);
        model.addAttribute("postId", id);
        return "posts/edit";
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable UUID id,
                             @Valid @ModelAttribute("postForm") PostForm postForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/edit";
        }
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("У вас нет прав на редактирование этого поста");
        }

        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        postService.updatePost(post, postForm.getHashtagsInput());
        return "redirect:/posts";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable UUID id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        PostEntity post = postService.findById(id)
                .orElseThrow(() -> new RuntimeException("Пост не найден"));
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAuthor = post.getUser().getUsername().equals(userDetails.getUsername());
        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("У вас нет прав на удаление этого поста");
        }
        postService.deletePost(id);
        return "redirect:/posts";
    }
}