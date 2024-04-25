package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import ru.practicum.api.event.EventShortDto;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventShortMapper extends DefaultMapper<Event, EventShortDto> {
}
