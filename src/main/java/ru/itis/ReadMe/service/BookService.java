package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import ru.itis.ReadMe.entity.BookEntity;
import ru.itis.ReadMe.repository.BookRepository;
import ru.itis.ReadMe.specification.BookSpecification;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Cacheable(value = "books", key = "{#title, #author, #genre, #minPages," +
            "#maxPages, #pageable.pageNumber, #pageable.pageSize}")
    public Page<BookEntity> findBooksWithFilters(String title, String author, String genre,
                                                 Integer minPages, Integer maxPages,
                                                 Pageable pageable) {
        Specification<BookEntity> spec = BookSpecification.withFilters(title, author, genre, minPages, maxPages);
        return bookRepository.findAll(spec, pageable);
    }

    @CacheEvict(value = "books", allEntries = true)
    public Optional<BookEntity> findById(UUID id) {
        return bookRepository.findById(id);
    }
}
