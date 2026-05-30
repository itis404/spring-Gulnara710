// service/GitHubOAuthService.java
package ru.itis.ReadMe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.ReadMe.dto.GitHubUserDto;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubOAuthService {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.github.oauth.client-id}")
    private String clientId;

    @Value("${app.github.oauth.client-secret}")
    private String clientSecret;

    @Value("${app.github.oauth.redirect-uri}")
    private String redirectUri;

    public String getAuthorizationUrl() {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=user:email";
    }

    public String getAccessToken(String code) throws IOException {
        String url = "https://github.com/login/oauth/access_token";

        RequestBody body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            }
            String json = response.body().string();
            JsonNode node = objectMapper.readTree(json);
            return node.get("access_token").asText();
        }
    }

    public GitHubUserDto getUserInfo(String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch user: " + response.code());
            }
            String json = response.body().string();
            return objectMapper.readValue(json, GitHubUserDto.class);
        }
    }
}
