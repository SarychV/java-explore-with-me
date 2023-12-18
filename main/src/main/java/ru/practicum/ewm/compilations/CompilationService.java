package ru.practicum.ewm.compilations;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;

import java.util.List;

public interface CompilationService {
    CompilationDtoOut addCompilation(CompilationDto compilationDto);

    List<CompilationDtoOut> getCompilations(Boolean pinned, int from, int size);

    CompilationDtoOut getCompilationById(long compId);

    CompilationDtoOut updateCompilation(long compId, CompilationDto compilationDto);

    void deleteCompilation(long compId);
}
