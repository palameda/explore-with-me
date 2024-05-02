package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestProcessingException;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.statistics.StatService;
import ru.practicum.service.statistics.Statistics;
import ru.practicum.utility.mapper.EventMapper;
import ru.practicum.utility.mapper.EventShortMapper;
import ru.practicum.utility.page.Page;

import javax.persistence.criteria.Join;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final LocalDateTime releaseAppDate = LocalDateTime.of(2024, 4, 26, 0, 0);
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper fullDtoMapper;
    private final EventShortMapper shortDtoMapper;
    private final StatService statService;

    @Statistics
    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categoryIds,
                                               Boolean paid,
                                               LocalDateTime start,
                                               LocalDateTime end,
                                               Boolean isAvailable,
                                               EventSort eventSort,
                                               Integer from,
                                               Integer size) {
        Specification<Event> spec = generateSpecification(categoryIds, paid, start, end, isAvailable, text);
        List<Event> events = eventRepository.findAll(spec, new Page(from, size, Sort.unsorted()));

        getAndSetViews(events);
        return List.copyOf(shortDtoMapper.toDto(events));
    }

    @Override
    public Collection<EventFullDto> getAll(Integer from, Integer size) {
        List<Event> events = eventRepository.findAll(new Page(from, size, Sort.unsorted())).getContent();

        getAndSetViews(events);
        return fullDtoMapper.toDto(events);
    }

    @Statistics
    @Override
    public EventFullDto get(Long id) {
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Не найден event id = " + id));

        getAndSetViews(List.of(event));
        return fullDtoMapper.toDto(event);
    }

    @Transactional
    @Override
    public EventFullDto confirmEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = Optional.ofNullable(eventId)
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено в системе")))
                .filter(e -> e.getState().equals(State.PENDING) && !e.getStateAction().equals(StateAction.REJECT_EVENT))
                .orElseThrow(() -> new DataConflictException("Условия для изменения события не выполнены"));

        handleAdminUpdate(event, request);
        eventRepository.save(event);
        return fullDtoMapper.toDto(event);
    }

    @Override
    public List<EventFullDto> getAllEventsForAdmins(List<Long> usersIds,
                                                    List<State> states,
                                                    List<Long> categoryIds,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    Integer from, Integer size) {
        Specification<Event> spec = generateAdminSpecification(usersIds, states, categoryIds, start, end);
        List<Event> events = eventRepository.findAll(spec, new Page(from, size, Sort.unsorted()));

        getAndSetViews(events);
        return List.copyOf(fullDtoMapper.toDto(events));
    }

    @Transactional
    @Override
    public EventFullDto create(Long userRequestFromId, NewEventDto newEventDto) {
        User initiator = userRepository
                .findById(userRequestFromId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userRequestFromId + " не найден в системе"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + newEventDto.getCategory() + " не найдена в системе"));
        Event event = Event.builder()
                .eventDate(newEventDto.getEventDate())
                .created(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .title(newEventDto.getTitle()).participantLimit(newEventDto.getParticipantLimit())
                .initiator(initiator)
                .category(category)
                .state(State.PENDING)
                .confirmedRequests(0L)
                .stateAction(StateAction.SEND_TO_REVIEW)
                .location(newEventDto.getLocation())
                .views(0L)
                .requestModeration(newEventDto.getRequestModeration())
                .annotation(newEventDto.getAnnotation())
                .paid(newEventDto.getPaid()).build();

        eventRepository.save(event);
        return fullDtoMapper.toDto(event);
    }

    @Override
    public Collection<EventShortDto> getAllByUser(Long initiatorId, Integer from, Integer size) {
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + initiatorId + " не найден в системе"));
        List<Event> events = eventRepository.findAllByInitiator(initiator, new Page(from, size, Sort.unsorted()));
        getAndSetViews(events);
        return shortDtoMapper.toDto(events);
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        Event event = Optional.ofNullable(eventId)
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено в системе")))
                .filter(e -> e.getInitiator().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Для события с id = " + eventId + "не найден указанный инициатор"));

        getAndSetViews(List.of(event));
        return fullDtoMapper.toDto(event);
    }

    @Transactional
    @Override
    public EventFullDto update(final Long userId, final Long eventId, UpdateEventUserRequest updateRequest) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден в системе"));
        Event event = Optional.ofNullable(eventId)
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено в системе")))
                .filter(e -> e.getInitiator().getId().equals(initiator.getId()))
                .orElseThrow(() -> new RequestProcessingException("Пользователь не является инициатором ивента"));

        handleUserUpdate(event, updateRequest);
        eventRepository.save(event);
        return fullDtoMapper.toDto(event);
    }

    private void handleRequestModeration(Event event, @Nullable Boolean newRequestModeration) {
        Optional.ofNullable(newRequestModeration)
                .ifPresent(event::setRequestModeration);
    }

    private void handleParticipantLimitUpdate(Event event, @Nullable Long newParticipantLimit) {
        Optional.ofNullable(newParticipantLimit)
                .ifPresent(event::setParticipantLimit);
    }

    private void handlePaidUpdate(Event event, @Nullable Boolean paid) {
        Optional.ofNullable(paid)
                .ifPresent(event::setPaid);
    }

    private void handleLocationUpdate(Event event, @Nullable Event.Location location) {
        Optional.ofNullable(location)
                .ifPresent(event::setLocation);
    }

    private void handleEventDateUpdate(Event event, @Nullable LocalDateTime newDate) {
        Optional.ofNullable(newDate)
                .ifPresent(event::setEventDate);
    }

    private void handleDescriptionUpdate(Event event, @Nullable String newDescription) {
        Optional.ofNullable(newDescription)
                .filter(d -> !d.isBlank())
                .ifPresent(event::setDescription);
    }

    private void handleAnnotationUpdate(Event event, @Nullable String newAnnotation) {
        Optional.ofNullable(newAnnotation)
                .filter(annotation -> !annotation.isBlank())
                .ifPresent(event::setAnnotation);
    }

    private void handleTitleUpdate(Event event, @Nullable String newTitle) {
        Optional.ofNullable(newTitle)
                .filter(title -> !title.isBlank())
                .ifPresent(event::setTitle);
    }

    private void handleCategoryUpdate(Event event, @Nullable Long newCategoryId) {
        Optional.ofNullable(newCategoryId)
                .filter(id -> !event.getCategory().getId().equals(id))
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена в системе")))
                .ifPresent(event::setCategory);
    }

    private void checkEventCanBeUpdated(Event event) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Нельзя внести изменения в событие, которое прошло модерацию");
        }
    }

    private void handleStateUpdate(Event event) {
        Optional.of(event)
                .filter(e -> e.getState().equals(State.PENDING))
                .ifPresent(e -> e.setStateAction(StateAction.SEND_TO_REVIEW));
    }

    private void setCorrectStateToEventFromAdminRequest(UpdateEventAdminRequest request, Event event) {
        if (request.getStateAction() == null) return;
        switch (request.getStateAction()) {
            case PUBLISH_EVENT: {
                event.setState(State.PUBLISHED);
                event.setPublished(LocalDateTime.now());
                event.setStateAction(StateAction.CANCEL_REVIEW);
                break;
            }
            case REJECT_EVENT: {
                event.setState(State.PENDING);
                event.setStateAction(StateAction.REJECT_EVENT);
                break;
            }
        }
    }

    private void setCorrectStateToEventFromUserRequest(UpdateEventUserRequest request, Event event) {
        if (request.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(State.CANCELED);
        }
    }

    private void handleUserUpdate(Event event, UpdateEventUserRequest updateRequest) {
        checkEventCanBeUpdated(event);
        handleStateUpdate(event);
        handleCategoryUpdate(event, updateRequest.getCategory());
        handleDescriptionUpdate(event, updateRequest.getDescription());
        handleEventDateUpdate(event, updateRequest.getEventDate());
        handleLocationUpdate(event, updateRequest.getLocation());
        handlePaidUpdate(event, updateRequest.getPaid());
        handleParticipantLimitUpdate(event, updateRequest.getParticipantLimit());
        handleRequestModeration(event, updateRequest.getRequestModeration());
        handleAnnotationUpdate(event, updateRequest.getAnnotation());
        handleTitleUpdate(event, updateRequest.getTitle());
        setCorrectStateToEventFromUserRequest(updateRequest, event);
    }

    private void handleAdminUpdate(Event event, UpdateEventAdminRequest adminRequest) {
        setCorrectStateToEventFromAdminRequest(adminRequest, event);
        handleCategoryUpdate(event, adminRequest.getCategory());
        handleDescriptionUpdate(event, adminRequest.getDescription());
        handleEventDateUpdate(event, adminRequest.getEventDate());
        handleLocationUpdate(event, adminRequest.getLocation());
        handlePaidUpdate(event, adminRequest.getPaid());
        handleParticipantLimitUpdate(event, adminRequest.getParticipantLimit());
        handleRequestModeration(event, adminRequest.getRequestModeration());
        handleAnnotationUpdate(event, adminRequest.getAnnotation());
        handleTitleUpdate(event, adminRequest.getTitle());
    }

    private void getAndSetViews(Collection<Event> events) {
        List<String> uris = events.stream()
                .map(ev -> "/events/" + ev.getId())
                .collect(Collectors.toList());
        var statCollection = statService.getStats(releaseAppDate, LocalDateTime.now(), uris, true);
        Map<Long, Long> eventIdAndHits = new HashMap<>();

        for (StatsDto statElem : statCollection) {
            eventIdAndHits.put(Long.valueOf(statElem.getUri().replaceAll("/events/", "")), statElem.getHits());
        }
        for (Event event : events) {
            event.setViews(eventIdAndHits.get(event.getId()));
        }
    }

    private Specification<Event> generateAdminSpecification(List<Long> initiators,
                                                          List<State> states,
                                                          List<Long> categories,
                                                          LocalDateTime start,
                                                          LocalDateTime end) {
        Specification<Event> specification = Specification.where(null);

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get("state").in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and(((root, query, cb) -> {
                Join<Event, Category> categoryJoin = root.join("category");
                return categoryJoin.get("id").in(categories);
            }));
        }
        if (start != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (initiators != null && (!initiators.isEmpty() && initiators.size() != 1 && !initiators.contains(0L))) {
            specification = specification.and(((root, query, cb) -> {
                Join<Event, User> userJoin = root.join("initiator");
                return userJoin.get("id").in(initiators);
            }));
        }
        if (end != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }
        return specification;
    }

    private Specification<Event> generateSpecification(List<Long> categoryIds,
                                                       Boolean paid,
                                                       LocalDateTime start,
                                                       LocalDateTime end,
                                                       Boolean isAvailable,
                                                       String text) {
        Specification<Event> specification = Specification.where((root, query, cb) -> cb.equal(root.get("state"), State.PUBLISHED));

        if (isAvailable != null && isAvailable) {
            specification = specification.and((root, query, cb) -> cb.greaterThan(root.get("participant_limit"), root.get("confirmed_requests")));
        }
        if (!text.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("annotation"), text));
        }
        if (paid != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("paid"), paid));
        }
        if (start != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }
        if (categoryIds != null) {
            specification = specification.and(((root, query, cb) -> {
                Join<Event, Category> categoryJoin = root.join("category");
                return categoryJoin.get("id").in(categoryIds);
            }));
        }
        return specification;
    }
}
