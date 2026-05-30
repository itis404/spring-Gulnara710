package ru.itis.ReadMe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
    private Set<String> hashtags;
    private long likesCount;
    private long dislikesCount;
}
