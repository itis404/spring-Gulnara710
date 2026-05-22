package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.DiscussionEntity;

import java.util.UUID;

@Repository
public interface DiscussionRepository extends JpaRepository<DiscussionEntity, UUID> {
}
