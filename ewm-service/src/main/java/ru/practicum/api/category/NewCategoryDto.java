package ru.practicum.api.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @Size(min = 1, max = 50, message = "Название категории должно содержать от 1 до 50 символов")
    @NotBlank
    private String name;
}
