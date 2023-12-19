package ru.practicum.ewm.compilations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;

    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  EventRepository eventRepository,
                                  StatisticService statisticService) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.statisticService = statisticService;
    }

    @Override
    public CompilationDtoOut addCompilation(CompilationDto compilationDto) {
        List<Long> eventIds = compilationDto.getEvents();
        List<Event> events;

        if (eventIds == null || eventIds.size() == 0) {
            events = new ArrayList<>();
        } else {
            events = eventRepository.findByIdIn(eventIds);
        }

        Compilation compilation = CompilationMapper.toCompilation(compilationDto, events);

        log.info("compilationRepository.save() was invoked with arguments compilation={}", compilation);
        Compilation returnedCompilation = compilationRepository.save(compilation);
        eventIds = returnedCompilation.getEvents()
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> stats = statisticService.receiveStatisticsByEventIds(eventIds, true);

        CompilationDtoOut result = CompilationMapper.toCompilationDtoOut(
                returnedCompilation, statisticService);
        log.info("In AdminCompilationsController was returned compilationDtoOut={}", result);
        return result;
    }

    @Override
    public List<CompilationDtoOut> getCompilations(Boolean pinned, int from, int size) {
        Page<Compilation> compPage;
        Pageable page = PageRequest.of(from, size);
        if (pinned == null) {
            compPage = compilationRepository.findAll(page);
        } else {
            compPage = compilationRepository.findByPinnedIs(pinned, page);
        }
        List<Compilation> compilations = compPage.stream().collect(Collectors.toList());
        List<CompilationDtoOut> result = CompilationMapper.toCompilationDtoOutList(compilations, statisticService);
        return result;
    }

    @Override
    public CompilationDtoOut getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                String.format("Compilation with id=%d was not found", compId)));
        CompilationDtoOut result = CompilationMapper.toCompilationDtoOut(compilation, statisticService);
        return result;
    }

    @Override
    public CompilationDtoOut updateCompilation(long compId, CompilationDto compilationDto) {
        Compilation previousCompilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        List<Long> eventIds = compilationDto.getEvents();
        List<Event> events;
        if (eventIds != null) {
            if (eventIds.size() == 0) {
                events = new ArrayList<>();
            } else {
                events = eventRepository.findByIdIn(eventIds);
            }
        } else {
            events = previousCompilation.getEvents();
        }

        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(previousCompilation.isPinned());
        }

        if (compilationDto.getTitle() == null) {
            compilationDto.setTitle(previousCompilation.getTitle());
        }

        Compilation compilation = CompilationMapper.toCompilation(compId, compilationDto, events);

        log.info("compilationRepository.save() was invoked with arguments compilation={}", compilation);
        Compilation returnedCompilation = compilationRepository.save(compilation);

        CompilationDtoOut result = CompilationMapper.toCompilationDtoOut(returnedCompilation, statisticService);
        log.info("To AdminCompilationsController was returned compilationDtoOUt={}", result);
        return result;
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                String.format("Compilation with id=%d was not found", compId)));
        log.info("Compilation with id={} was deleted", compId);
        compilationRepository.deleteById(compId);
    }
}
