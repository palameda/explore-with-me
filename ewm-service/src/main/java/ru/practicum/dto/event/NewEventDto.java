package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.model.Event;
import ru.practicum.utility.validation.StartTimeValidation;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Поле annotation должно содержать от 20 до 2000 символов")
    @NotBlank
    private String annotation;
    @NotNull
    @Positive
    private Long category;
    @Size(min = 20, max = 7000, message = "Поле description должно содержать от 20 до 7000 символов")
    @NotBlank
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @StartTimeValidation
    private LocalDateTime eventDate;
    @NotNull
    private Event.Location location;
    private Boolean paid = false;
    @PositiveOrZero
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    @Size(min = 3, max = 120, message = "Поле title должно содержать от 20 до 7000 символов")
    @NotBlank
    private String title;
}
