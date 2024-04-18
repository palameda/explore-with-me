package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Event;
import ru.practicum.repository.StatsDbRepository;
import ru.practicum.utility.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private StatsDbRepository repository;
    private Mapper mapper;

    @Transactional
    @Override
    public void save(EventDto eventDto) {
        if (eventDto.getCreated() == null) {
            eventDto.setCreated(LocalDateTime.now());
        }
        Event event = repository.save(mapper.toModel(eventDto));
        log.info("Запрос на сохранение {} успешно обработан", event);
    }

    @Override
    public List<StatsDto> findStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return unique ? findStatsByUniqueIp(start, end) : findAllStats(start, end);
        } else {
            return unique ? findStatsByUrisAndUniqueIp(start, end, uris) : findStatsByUris(start, end, uris);
        }
    }

    private List<StatsDto> findStatsByUniqueIp(LocalDateTime start, LocalDateTime end) {
        log.info("Запрос на получение статистики по уникальному ip");
        return repository.findStatisticsByUniqueIp(start, end);
    }

    private List<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end) {
        log.info("Запрос на получение статистики");
        return repository.findAllStatistics(start, end);
    }

    private List<StatsDto> findStatsByUrisAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.info("Запрос на получение статистики по uri и по уникальному ip");
        return repository.findStatisticsByUrisByUniqueIp(start, end, uris);
    }

    private List<StatsDto> findStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.info("Запрос на получение статистики по uri");
        return repository.findStatisticsByUris(start, end, uris);
    }
}
