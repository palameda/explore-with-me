package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.logs.ControllerLog;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentUserController {
    private final CommentService commentService;

    @ControllerLog
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Min(0) Long userId,
                             @RequestParam @Min(0) Long eventId,
                             @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.create(userId, eventId, newCommentDto);
    }

    @ControllerLog
    @PatchMapping("/{commentId}")
    public CommentDto updateOwn(@PathVariable @Min(0) Long userId,
                                @PathVariable @Min(0) Long commentId,
                                @RequestBody @Valid NewCommentDto updateDto) {
        return commentService.updateByUser(userId, commentId, updateDto);
    }

    @ControllerLog
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOwn(@PathVariable @Min(0) Long userId,
                          @PathVariable @Min(0) Long commentId) {
        commentService.deleteByUser(userId, commentId);
    }
}
