package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.logs.ControllerLog;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.DateParameterException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.EventSort;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final DateTimeFormatter formatter;

    @ControllerLog
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable("id") Long id) {
        return eventService.get(id);
    }

    @ControllerLog
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEvents(@RequestParam(value = "text", defaultValue = "") String text,
                                               @RequestParam(value = "categories", required = false) List<Long> categories,
                                               @RequestParam(value = "paid", required = false) Boolean paid,
                                               @RequestParam(value = "rangeStart", required = false) String startString,
                                               @RequestParam(value = "rangeEnd", required = false) String endString,
                                               @RequestParam(value = "onlyAvailable", required = false) Boolean isAvailable,
                                               @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        EventSort eventSort = EventSort.from(sort)
                .orElseThrow(() -> new NotFoundException("Неизвестный параметр сортировки: " + sort));
        LocalDateTime start = Optional.ofNullable(startString)
                .map(string -> LocalDateTime.parse(string, formatter))
                .orElse(LocalDateTime.now());
        LocalDateTime end = Optional.ofNullable(endString)
                .map(string -> LocalDateTime.parse(string, formatter))
                .orElse(start.plusYears(100));

        if (!end.isAfter(start)) {
            throw new DateParameterException("Указан некорректный временной промежуток");
        }

        return eventService.getPublicEvents(
                text,
                categories,
                paid,
                start,
                end,
                isAvailable,
                eventSort,
                from,
                size
        );
    }
}
