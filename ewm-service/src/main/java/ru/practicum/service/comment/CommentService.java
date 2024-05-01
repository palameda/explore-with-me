package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.utility.crud.user.UserRequestCreateService;

import java.util.List;

public interface CommentService {

    List<CommentDto> getEventComments(Long eventId, Integer from, Integer size);

    CommentDto create(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto updateByUser(long userId, long commentId, NewCommentDto updateDto);

    CommentDto updateByAdmin(long commentId, NewCommentDto updateDto);

    void deleteByUser(long userId, long commentId);

    void deleteByAdmin(long commentId);
}
