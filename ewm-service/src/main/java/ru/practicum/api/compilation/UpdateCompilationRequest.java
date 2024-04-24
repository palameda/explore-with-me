package ru.practicum.api.compilation;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50, message = "Поле title должно содержать от 1 до 50 символов")
    private String title;
}
