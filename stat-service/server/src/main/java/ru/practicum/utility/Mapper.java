package ru.practicum.utility;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventDto;
import ru.practicum.model.Event;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final ModelMapper modelMapper;
    public Event toModel(EventDto eventDto) {
        return modelMapper.map(eventDto, Event.class);
    }
}
