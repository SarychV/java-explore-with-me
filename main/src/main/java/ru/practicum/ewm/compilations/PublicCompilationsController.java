package ru.practicum.ewm.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final CompilationService compilationService;

    public PublicCompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDtoOut> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("compilationService.getCompilation() was invoked with arguments pinned={}, from={}, size={}",
                pinned, from, size);

        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDtoOut getCompilationById(@PathVariable @Positive Long compId) {
        log.info("compilationService.getCompilationById() was invoked with arguments compId={}", compId);

        return compilationService.getCompilationById(compId);
    }
}
