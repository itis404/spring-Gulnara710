package ru.itis.ReadMe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.repository.BookRepository;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.google-books.api-url}")
    private String apiUrl;

    @Value("${app.google-books.api-key}")
    private String apiKey;

    public List<BookEntity> searchBooks(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = apiUrl + "?q=" + encodedQuery + "&maxResults=20&key=" + apiKey;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("Google Books API error: {} - {}", response.code(), errorBody);
                throw new IOException("Unexpected code " + response.code());
            }

            String json = response.body().string();
            JsonNode root = mapper.readTree(json);
            JsonNode items = root.get("items");

            if (items == null || !items.isArray()) {
                log.info("No items found for query: {}", query);
                return Collections.emptyList();
            }

            List<BookEntity> books = new ArrayList<>();
            for (JsonNode item : items) {
                JsonNode volumeInfo = item.get("volumeInfo");
                if (volumeInfo != null) {
                    BookEntity book = BookEntity.builder()
                            .title(volumeInfo.has("title") ? volumeInfo.get("title").asText() : "Без названия")
                            .author(volumeInfo.has("authors") && volumeInfo.get("authors").size() > 0
                                    ? volumeInfo.get("authors").get(0).asText() : "Неизвестен")
                            .description(volumeInfo.has("description") ? volumeInfo.get("description").asText() : null)
                            .genre(volumeInfo.has("categories") && volumeInfo.get("categories").size() > 0
                                    ? volumeInfo.get("categories").get(0).asText() : null)
                            .pages(volumeInfo.has("pageCount") ? volumeInfo.get("pageCount").asInt() : null)
                            .publishedYear(volumeInfo.has("publishedDate") ? parseYear(volumeInfo.get("publishedDate").asText()) : null)
                            .coverUrl(volumeInfo.has("imageLinks") && volumeInfo.get("imageLinks").has("thumbnail")
                                    ? volumeInfo.get("imageLinks").get("thumbnail").asText() : null)
                            .build();
                    books.add(book);
                }
            }
            return books;
        }
    }

    private Integer parseYear(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return Integer.parseInt(dateStr.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @CacheEvict(value = {"books", "book"}, allEntries = true)
    public BookEntity save(BookEntity book) {
        return bookRepository.save(book);
    }

    @CacheEvict(value = {"books", "book"}, allEntries = true)
    public BookEntity importBookToDatabase(BookEntity book) {
        return bookService.save(book);
    }

    @Autowired
    private CacheManager cacheManager;

    public void evictAllBookCaches() {
        cacheManager.getCache("books").clear();
        cacheManager.getCache("book").clear();
    }
}
