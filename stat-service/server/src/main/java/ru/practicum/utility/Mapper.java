package ru.practicum.utility;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;
import ru.practicum.model.Hit;

@Component
@RequiredArgsConstructor
public class Mapper {
    private final ModelMapper modelMapper;

    public Hit toModel(HitDto hitDto) {
        return modelMapper.map(hitDto, Hit.class);
    }
}
