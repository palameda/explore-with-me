package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.logs.ControllerLog;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @ControllerLog
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @ControllerLog
    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest,
                                            @PathVariable("compilationId") Long id) {
        return compilationService.update(id, updateCompilationRequest);
    }

    @ControllerLog
    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compilationId") Long id) {
        compilationService.delete(id);
    }
}
