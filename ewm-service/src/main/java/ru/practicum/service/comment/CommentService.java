package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.utility.crud.user.UserRequestCreateService;

import java.util.List;

public interface CommentService extends UserRequestCreateService<CommentDto,Long, NewCommentDto> {
    List<CommentDto> getEventComments(Long eventId, Integer from, Integer size);
}
