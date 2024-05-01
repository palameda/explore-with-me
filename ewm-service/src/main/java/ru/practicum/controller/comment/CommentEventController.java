package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.comment.CommentService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentEventController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable @Min(0) Long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "5") @Positive Integer size) {
        return commentService.getEventComments(eventId, from, size);
    }
}
