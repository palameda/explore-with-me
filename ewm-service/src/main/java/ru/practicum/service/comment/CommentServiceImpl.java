package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.ForbiddenActionException;
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

    @Override
    public CommentDto create(Long userId, NewCommentDto commentDto) {
        User author = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден в системе"));
        Event event = eventRepository
                .findById(commentDto.getEvent_id())
                .orElseThrow(() -> new NotFoundException("Событие с id = " + commentDto.getEvent_id() + " не найдено в системе"));
        handleCommentAvailability(event);

        Comment comment = Comment.builder()
                .eventId(commentDto.getEvent_id())
                .author(author)
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
        log.info("Добавление комментария от {} к событию {}", author, event);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private void handleCommentAvailability(Event event) {
        Optional.ofNullable(event)
                .filter(e -> !e.getState().equals(State.PENDING))
                .orElseThrow(() -> new ForbiddenActionException("Комментировать можно только те события, " +
                        "которые находятся в состоянии ожидания"));
    }
}
