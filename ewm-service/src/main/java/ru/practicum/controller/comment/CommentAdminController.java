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
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @ControllerLog
    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable @Min(0) Long commentId,
                             @RequestBody @Valid NewCommentDto updateDto) {
        return commentService.updateByAdmin(commentId, updateDto);
    }

    @ControllerLog
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(0) Long commentId) {
        commentService.deleteByAdmin(commentId);
    }
}
