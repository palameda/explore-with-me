package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper extends DefaultMapper<Event, EventFullDto> {
}
