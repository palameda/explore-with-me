package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.logs.ControllerLog;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @ControllerLog
    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(value = "pinned", defaultValue = "false") Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return List.copyOf(compilationService.getCompilationsByPinned(pinned, from, size));
    }

    @ControllerLog
    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable("compId") Long compId) {
        return compilationService.get(compId);
    }
}
