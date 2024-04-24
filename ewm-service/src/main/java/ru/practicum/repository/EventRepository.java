package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    List<Event> findAllByCategory(Category category);

    List<Event> findAllByInitiator(User initiator, Pageable pageable);

    Optional<Event> findByIdAndState(Long id, State state);
}
