package ru.practicum.ewm.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationService compilationService;

    public AdminCompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDtoOut addCompilation(@RequestBody @Valid CompilationDto compilationDto) {
        String title = compilationDto.getTitle();
        if (title == null || title.isBlank()) {
            throw new BadRequestException(
                    String.format("Field: title. Error: must not be blank. Value: %s", title));
        }
        log.info("compilationService.addCompilation() was invoked with arguments compilationDto={}",
                compilationDto);
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDtoOut updateCompilation(
            @PathVariable @Positive long compId,
            @RequestBody @Valid CompilationDto compilationDto) {
        log.info("compilationService.updateCompilation() was invoked with arguments compId={}, compilationDto={}",
                compId, compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive long compId) {
        log.info("compilationService.deleteCompilation() was invoked with compId={}", compId);
        compilationService.deleteCompilation(compId);
    }
}


