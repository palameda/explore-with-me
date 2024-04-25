package ru.practicum.service.compilation;

import ru.practicum.api.compilation.CompilationDto;
import ru.practicum.api.compilation.NewCompilationDto;
import ru.practicum.utility.crud.DefaultCreateService;
import ru.practicum.utility.crud.DefaultDeleteService;
import ru.practicum.utility.crud.DefaultGetService;
import ru.practicum.utility.crud.DefaultUpdateService;
import ru.practicum.utility.crud.compilation.PinnedCompilationGetService;

public interface CompilationService extends
        DefaultCreateService<NewCompilationDto, CompilationDto>,
        DefaultUpdateService<NewCompilationDto, CompilationDto>,
        DefaultGetService<Long, CompilationDto>,
        PinnedCompilationGetService<CompilationDto>,
        DefaultDeleteService<Long> {
}
