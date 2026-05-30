package ru.itis.ReadMe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.ReadMe.dto.PopularHashtagDto;
import ru.itis.ReadMe.repository.HashtagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public List<PopularHashtagDto> getPopularHashtagsAboveAverage() {
        return hashtagRepository.findPopularHashtagsAboveAverage()
                .stream()
                .map(row -> new PopularHashtagDto((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }
}
