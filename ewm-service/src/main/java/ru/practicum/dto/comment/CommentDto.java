package ru.practicum.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private Long event_id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
