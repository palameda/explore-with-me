package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper extends DefaultMapper<Comment, CommentDto> {
    @Override
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toDto(Comment comment);
}
