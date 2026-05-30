package ru.itis.ReadMe.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.itis.ReadMe.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<BookEntity> withFilters(
            String title,
            String author,
            String genre,
            Integer minPages,
            Integer maxPages) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (author != null && !author.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (genre != null && !genre.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
            }
            if (minPages != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pages"), minPages));
            }
            if (maxPages != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pages"), maxPages));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
