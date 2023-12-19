package ru.practicum.ewm.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.map.DateMapper;
import ru.practicum.ewm.stats.service.exception.BadRequestException;
import ru.practicum.ewm.stats.service.model.StatEntry;
import ru.practicum.ewm.stats.service.model.StatResult;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository repository) {
        this.statsRepository = repository;
    }

    @Override
    public void saveStatisticEntry(StatEntryDto entryDto) {
        StatEntry entry = StatsMapper.toStatEntry(entryDto);
        entry = statsRepository.save(entry);
        log.info("Выполнено сохранение в базу данных записи статистики с параметрами entry={}", entry);
    }

    @Override
    public List<StatResult> getStatisticByUris(String start, String end, List<String> uris, boolean unique) {
        List<StatResult> result = new ArrayList<>();

        LocalDateTime startDate = DateMapper.toLocalDateTime(start);
        LocalDateTime endDate = DateMapper.toLocalDateTime(end);

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException(String.format("Error: startDate=%s is after endDate=%s",
                    startDate.toString(), endDate.toString()));
        }

        log.info("GET-запрос к базе данных с параметрами startDate={}, endDate={}, uris={}", startDate, endDate, uris);
        if (unique) {
            if (uris == null) {
                result = statsRepository.findAllUrisAndUniqueIP(startDate, endDate);
            } else {
                result = statsRepository.findUrisAndUniqueIP(startDate, endDate, uris);
            }
        } else {
            if (uris == null) {
                result = statsRepository.findAllUris(startDate, endDate);
            } else {
                result = statsRepository.findUris(startDate, endDate, uris);
            }
        }
        log.info("По GET-запросу возвращено StatResult={}", result);
        return result;
    }
}
