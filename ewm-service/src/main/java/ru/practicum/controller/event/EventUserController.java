package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.logs.ControllerLog;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventUserController {
    private final EventService eventService;

    @ControllerLog
    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByUser(@PathVariable("userId") Long userId,
                                          @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @ControllerLog
    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByInitiator(@PathVariable("userId") Long initiatorId,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return List.copyOf(eventService.getAllByUser(initiatorId, from, size));
    }

    @ControllerLog
    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable("userId") Long userId,
                                     @PathVariable("eventId") Long eventId) {
        return eventService.get(userId, eventId);
    }

    @ControllerLog
    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.update(userId, eventId, updateEventUserRequest);
    }
}
