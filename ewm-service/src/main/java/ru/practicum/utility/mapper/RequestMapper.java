package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.api.request.ParticipationRequestDto;
import ru.practicum.model.EventParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper extends DefaultMapper<EventParticipationRequest, ParticipationRequestDto> {
    @Override
    @Mapping(target = "event", source = "entity.event.id")
    @Mapping(target = "requester", source = "entity.requester.id")
    ParticipationRequestDto toDTO(EventParticipationRequest entity);
}
