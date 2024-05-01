package ru.practicum.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank(message = "Поле комментария не должно быть пустым")
    @Size(max = 2000, message = "Размер комментария не должен превышать 2000 символов")
    private String text;
}
