package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;
import ru.practicum.model.EventParticipationRequest;
import ru.practicum.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<EventParticipationRequest, Long> {
    List<EventParticipationRequest> findAllByEvent(Event event);

    List<EventParticipationRequest> findAllByRequester(User requester);
}
