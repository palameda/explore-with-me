package ru.practicum.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class NewCommentDto {
    @NotNull
    @PositiveOrZero
    private Long event_id;
    @NotBlank
    private String text;
}
