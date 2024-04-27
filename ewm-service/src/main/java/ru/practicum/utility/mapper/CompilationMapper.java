package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper extends DefaultMapper<Compilation, CompilationDto> {
}
