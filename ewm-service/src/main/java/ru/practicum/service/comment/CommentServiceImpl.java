package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DenialOfAccessException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.utility.mapper.CommentMapper;
import ru.practicum.utility.page.Page;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getEventComments(Long eventId, Integer from, Integer size) {
        List<Comment> comments = Optional.ofNullable(eventId)
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено в системе")))
                .map(event -> commentRepository.findAllByEventId(event.getId(), new Page(from, size, Sort.unsorted())))
                .orElse(Collections.emptyList());
        log.info("Получение {} комментария(ев) к событию", comments.size());
        return List.copyOf(commentMapper.toDto(comments));
    }

    @Transactional
    @Override
    public CommentDto create(long userId, long eventId, NewCommentDto newCommentDto) {
        User author = getUserById(userId);
        Event event = getEventById(eventId);
        handleCommentAvailability(event);
        Comment comment = commentRepository.save(commentMapper.toModel(newCommentDto, event, author));
        log.info("Добавлен комментарий от пользователя {} к событию {}", author, event);
        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateByUser(long userId, long commentId, NewCommentDto updateDto) {
        Comment comment = getCommentById(commentId);
        handleCommentAuthorship(comment, userId);
        comment.setText(updateDto.getText());
        comment.setEdited(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Комментарий {} упешно отредактирован автором", comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateByAdmin(long commentId, NewCommentDto updateDto) {
        Comment comment = getCommentById(commentId);
        comment.setText(updateDto.getText());
        comment.setEdited(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Комментарий {} упешно отредактирован администратором", comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public void deleteByUser(long userId, long commentId) {
        Comment comment = getCommentById(commentId);
        handleCommentAuthorship(comment, userId);
        commentRepository.deleteById(commentId);
        log.info("Комментарий {} удалён автором", comment);
    }

    @Transactional
    @Override
    public void deleteByAdmin(long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.deleteById(commentId);
        log.info("Комментарий {} удалён администратором", comment);
    }

    private void handleCommentAvailability(Event event) {
        Optional.ofNullable(event)
                .filter(e -> !e.getState().equals(State.PENDING))
                .orElseThrow(() -> new DataConflictException("Комментировать можно только те события, " +
                        "которые не находятся в состоянии ожидания"));
    }

    private void handleCommentAuthorship(Comment comment, Long userId) {
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new DenialOfAccessException("Пользователь с id = " + userId + " не является автором комментария");
        }
    }

    private User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден в системе"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено в системе"));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден в системе"));
    }
}
