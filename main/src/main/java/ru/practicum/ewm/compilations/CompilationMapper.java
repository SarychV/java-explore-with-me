package ru.practicum.ewm.compilations;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {
    protected static Compilation toCompilation(CompilationDto dto, List<Event> events) {
        Compilation result = new Compilation();

        Boolean requestPinned = dto.getPinned();
        if (requestPinned != null) {
            result.setPinned(requestPinned);
        } else {
            result.setPinned(false);
        }

        result.setTitle(dto.getTitle());
        result.setEvents(events);
        return result;
    }

    protected static Compilation toCompilation(long compId, CompilationDto dto, List<Event> events) {
        Compilation result = new Compilation();

        Boolean requestPinned = dto.getPinned();
        if (requestPinned != null) {
            result.setPinned(requestPinned);
        } else {
            result.setPinned(false);
        }

        result.setId(compId);
        result.setTitle(dto.getTitle());
        result.setEvents(events);
        return result;
    }

    protected static CompilationDtoOut toCompilationDtoOut(Compilation compilation,
               StatisticService statisticService) {
        CompilationDtoOut result = new CompilationDtoOut();
        result.setId(compilation.getId());
        result.setTitle(compilation.getTitle());
        result.setPinned(compilation.isPinned());
        List<Long> eventIds = compilation.getEvents()
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, true);

        result.setEvents(
                compilation.getEvents()
                        .stream()
                        .map(e -> EventMapper.toEventShortDtoOut(e, stats.get(e.getId())))
                        .collect(Collectors.toList()));
        return result;
    }

    protected static List<CompilationDtoOut> toCompilationDtoOutList(List<Compilation> compilations,
             StatisticService statisticService) {
        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDtoOut(compilation, statisticService))
                .collect(Collectors.toList());
    }
}
