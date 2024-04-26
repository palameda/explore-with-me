package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.api.request.EventRequestStatusUpdateRequest;
import ru.practicum.api.request.EventRequestStatusUpdateResult;
import ru.practicum.api.request.ParticipationRequestDto;
import ru.practicum.exception.ConfirmationException;
import ru.practicum.exception.ForbiddenActionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestProcessingException;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.utility.mapper.RequestMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = Optional.ofNullable(eventId)
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено в системе")))
                .filter(ev -> !ev.getInitiator().getId().equals(userId))
                .filter(ev -> ev.getState().equals(State.PUBLISHED))
                .filter(ev -> ev.getParticipantLimit() > ev.getConfirmedRequests() || ev.getParticipantLimit() == 0)
                .filter(ev -> !requestRepository.findAllByEvent(ev).stream()
                        .map(request -> request.getRequester().getId())
                        .collect(Collectors.toList())
                        .contains(userId))
                .orElseThrow(() -> new RequestProcessingException("Неверные параметры в запросе на участие"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден в системе"));
        EventParticipationRequest request = getNewRequestModel(user, event);

        requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsToUserEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден в системе"));
        Event event = eventRepository.findById(eventId)
                .filter(ev -> ev.getInitiator().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено в системе"));

        return List.copyOf(requestMapper.toDto(requestRepository.findAllByEvent(event)));
    }

    @Override
    public EventRequestStatusUpdateResult confirmRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден user id = " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найден event id = " + eventId));

        checkUserCanConfirmRequests(user, event);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConfirmationException("Отключена модерация заявок или отсутсвует лимит на участников");
        }

        List<EventParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        return handleEventRequests(event, requests, updateRequest.getStatus());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EventParticipationRequest request = Optional.ofNullable(requestId)
                .map(id -> requestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Запрос с id = " + id + " не найден в системе")))
                .filter(req -> req.getRequester().getId().equals(userId))
                .orElseThrow(() -> new ForbiddenActionException("Запрос не принадлежит пользователю"));

        request.setStatus(Status.CANCELED);
        requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        var requests = Optional.ofNullable(userId)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userId)))
                .map(requestRepository::findAllByRequester)
                .orElse(Collections.emptyList());
        return List.copyOf(requestMapper.toDto(requests));
    }

    private EventRequestStatusUpdateResult handleEventRequests(Event event, List<EventParticipationRequest> requests, Status status) {
        Long participantLimit = event.getParticipantLimit();
        Long confirmedRequests = event.getConfirmedRequests();

        if (participantLimit.equals(confirmedRequests)) {
            throw new ForbiddenActionException("Достигнут лимит заявок");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(new ArrayList<>());
        result.setRejectedRequests(new ArrayList<>());
        Iterator<EventParticipationRequest> iterator = requests.iterator();

        while (confirmedRequests < participantLimit && iterator.hasNext()) {
            EventParticipationRequest request = iterator.next();
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ForbiddenActionException("Заявка c id = " + request.getId() + " уже была рассмотрена");
            }
            request.setStatus(status);
            if (status.equals(Status.CONFIRMED)) {
                confirmedRequests++;
            }
        }

        while (iterator.hasNext()) {
            EventParticipationRequest request = iterator.next();
            request.setStatus(Status.REJECTED);
        }

        for (EventParticipationRequest request : requests) {
            if (request.getStatus().equals(Status.CONFIRMED)) {
                result.getConfirmedRequests().add(requestMapper.toDto(request));
            } else {
                result.getRejectedRequests().add(requestMapper.toDto(request));
            }
        }

        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        return result;
    }


    private void checkUserCanConfirmRequests(User user, Event event) {
        Long userId = user.getId();
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenActionException("Пользователь не является создателем ивента");
        }
    }

    private EventParticipationRequest getNewRequestModel(User requester, Event event) {
        EventParticipationRequest request = new EventParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
            Long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(++confirmedRequests);
            eventRepository.save(event);
        } else {
            request.setStatus(Status.PENDING);
        }
        return request;
    }
}
