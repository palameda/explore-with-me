package ru.practicum.service;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void save(EventDto eventDto);

    List<StatsDto> findAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
