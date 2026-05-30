package ru.itis.ReadMe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itis.ReadMe.entity.HashtagEntity;

import java.util.*;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<HashtagEntity, UUID> {

    Optional<HashtagEntity> findByNameIgnoreCase(String name);

    @Query(value = """
    SELECT h.name, COUNT(p.id) as cnt 
    FROM hashtags h 
    JOIN post_hashtag ph ON h.id = ph.hashtag_id 
    JOIN posts p ON ph.post_id = p.id 
    GROUP BY h.id 
    HAVING COUNT(p.id) > (
        SELECT AVG(t.cnt) FROM (
            SELECT COUNT(p2.id) as cnt 
            FROM hashtags h2 
            JOIN post_hashtag ph2 ON h2.id = ph2.hashtag_id 
            JOIN posts p2 ON ph2.post_id = p2.id 
            GROUP BY h2.id
        ) t
    )
    ORDER BY cnt DESC 
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findPopularHashtagsAboveAverage();
}