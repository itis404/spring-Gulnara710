package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.BookEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, UUID>, JpaSpecificationExecutor<BookEntity> {
    Optional<BookEntity> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);
}
