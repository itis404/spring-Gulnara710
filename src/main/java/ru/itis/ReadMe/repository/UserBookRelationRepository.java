package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.UserBookRelation;
import ru.itis.ReadMe.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBookRelationRepository extends JpaRepository<UserBookRelation, UUID> {

    Optional<UserBookRelation> findByUserIdAndBookId(UUID userId, UUID bookId);
    List<UserBookRelation> findByUserAndStatus(UserEntity user, UserBookRelation.Status status);
}
