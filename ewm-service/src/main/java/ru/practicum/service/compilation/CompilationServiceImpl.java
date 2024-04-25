package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.api.compilation.CompilationDto;
import ru.practicum.api.compilation.NewCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.utility.mapper.CompilationMapper;
import ru.practicum.utility.page.Page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto create(NewCompilationDto createDto) {
        Compilation compilation = Compilation.builder()
                .title(createDto.getTitle())
                .pinned(createDto.getPinned() != null && createDto.getPinned())
                .build();
        setEvents(compilation, createDto.getEvents());
        compilationRepository.save(compilation);
        log.info("Подборка {} успешно сохранена", compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public void delete(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не найдена в системе"));
        compilationRepository.delete(compilation);
        log.info("Подборка с id = {} успешно удалена", id);
    }

    @Override
    public Collection<CompilationDto> getAll(Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAll(new Page(from, size, Sort.unsorted()))
                .getContent();
        log.info("Получение списка подборок размером {}", compilations.size());
        return compilationMapper.toDto(compilations);
    }

    @Override
    public CompilationDto get(Long id) {
        log.info("Получение подборки по id = {}", id);
        return compilationRepository.findById(id)
                .map(compilationMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не найдена в системе"));
    }

    @Override
    public CompilationDto update(Long id, NewCompilationDto updateDto) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + id + " не найдена в системе"));
        updateTitle(compilation, updateDto.getTitle());
        updatePinned(compilation, updateDto.getPinned());
        updateEvents(compilation, updateDto.getEvents());
        compilationRepository.save(compilation);
        log.info("Подборка {} успешно обновлена", compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public Collection<CompilationDto> getCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        log.info("Получение коллекции закреплённых подборок с pinned = {}", pinned);
        return Optional.ofNullable(pinned)
                .map(p -> compilationRepository.findCompilationByPinned(p, new Page(from, size, Sort.unsorted())))
                .map(compilationMapper::toDto)
                .orElse(Collections.emptyList());
    }

    private void updateTitle(Compilation compilation, @Nullable String title) {
        Optional.ofNullable(title)
                .ifPresent(compilation::setTitle);
    }

    private void updatePinned(Compilation compilation, @Nullable Boolean pinned) {
        Optional.ofNullable(pinned)
                .ifPresent(compilation::setPinned);
    }

    private void updateEvents(Compilation compilation, @Nullable List<Long> eventsIds) {
        Optional.ofNullable(eventsIds)
                .filter(ids -> !ids.equals(
                        compilation.getEvents()
                                .stream()
                                .map(Event::getId)
                                .collect(Collectors.toList())
                ))
                .map(eventRepository::findAllById)
                .ifPresent(compilation::setEvents);
    }

    private void setEvents(Compilation compilation, @Nullable Collection<Long> eventsIds) {
        Optional.ofNullable(eventsIds)
                .map(eventRepository::findAllById)
                .ifPresent(compilation::setEvents);
    }
}
