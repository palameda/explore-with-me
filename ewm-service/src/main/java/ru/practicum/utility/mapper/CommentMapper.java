package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface CommentMapper extends DefaultMapper<Comment, CommentDto> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(target = "edited", ignore = true)
    Comment toModel(NewCommentDto newCommentDto, Event event, User author);

    @Override
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toDto(Comment comment);
}
