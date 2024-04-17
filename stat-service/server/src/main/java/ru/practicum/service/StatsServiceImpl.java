package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Event;
import ru.practicum.repository.StatsDbRepository;
import ru.practicum.utility.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsDbRepository repository;
    private final Mapper mapper;

    @Override
    public void save(EventDto eventDto) {
        repository.save(mapper.toModel(eventDto));
        log.info("Запрос на сохранение {} успешно обработан", eventDto);
    }

    @Override
    public List<StatsDto> findAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Запрос статистики, где start - {}, end - {}, uris = {}, unique = {}", start, end, uris, unique);
        if (uris == null || uris.isEmpty()) {
            uris = repository.findAll().stream()
                    .map(Event::getUri)
                    .collect(Collectors.toList());
        }
        return unique ? repository.findUniqueStatistics(start, end, uris) : repository.findRequiredStatistics(start, end, uris);
    }
}
