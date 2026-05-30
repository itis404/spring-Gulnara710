package ru.itis.ReadMe.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.ReadMe.dto.GitHubUserDto;
import ru.itis.ReadMe.entity.UserEntity;
import ru.itis.ReadMe.security.CustomUserDetailsService;
import ru.itis.ReadMe.service.GitHubOAuthService;
import ru.itis.ReadMe.service.UserService;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final GitHubOAuthService gitHubOAuthService;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/github")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        response.sendRedirect(gitHubOAuthService.getAuthorizationUrl());
    }

    @GetMapping("/github/callback")
    public String githubCallback(@RequestParam("code") String code,
                                 HttpServletRequest request) {
        try {
            String accessToken = gitHubOAuthService.getAccessToken(code);
            GitHubUserDto gitHubUser = gitHubOAuthService.getUserInfo(accessToken);

            String email = gitHubUser.getEmail();
            if (email == null || email.isBlank()) {
                email = gitHubUser.getLogin() + "@users.noreply.github.com";
            }
            final String finalEmail = email;
            final String finalUsername = gitHubUser.getLogin();

            UserEntity user = userService.findByEmail(finalEmail).orElseGet(() -> {
                String generatedPassword = "oauth2_" + System.currentTimeMillis();
                return userService.registerUser(finalUsername, finalEmail, generatedPassword);
            });

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            log.info("Пользователь {} вошёл через GitHub", user.getUsername());
            return "redirect:/posts";

        } catch (Exception e) {
            log.error("Ошибка при входе через GitHub", e);
            return "redirect:/login?error=oauth";
        }
    }
}
