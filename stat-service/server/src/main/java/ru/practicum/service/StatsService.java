package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void save(HitDto hitDto);

    List<StatsDto> findStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
