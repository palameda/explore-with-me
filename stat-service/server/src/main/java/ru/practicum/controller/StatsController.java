package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;
    private final DateTimeFormatter dateTimeFormatter;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody EventDto eventDto) {
        log.info("Контроллер: получен POST метод запроса по эндпоинту /hit с {}", eventDto);
        service.save(eventDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDto> getStats(@RequestParam(value = "start") @NotBlank String startTimeString,
                                   @RequestParam(value = "end") @NotBlank String endTimeString,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Контроллер: получен GET метод запроса по эндпоинту /stats c start = {}, end = {}, uris = {}, unique = {}",
                startTimeString, endTimeString, uris, unique
        );
        LocalDateTime start = LocalDateTime.parse(startTimeString, dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse(endTimeString, dateTimeFormatter);
        if (end.isBefore(start)) {
            throw new BadRequestException("Указаны некорректные значения для даты/времени");
        }
        return service.findStatistics(start, end, uris, unique);
    }
}
