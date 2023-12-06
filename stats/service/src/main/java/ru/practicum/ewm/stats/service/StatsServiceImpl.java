package ru.practicum.ewm.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.StatEntryDto;
import ru.practicum.ewm.stats.map.DateMapper;
import ru.practicum.ewm.stats.service.model.StatEntry;
import ru.practicum.ewm.stats.service.model.StatResult;

import java.util.List;
import java.time.LocalDateTime;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository repository) {
        this.statsRepository = repository;
    }

    @Override
    public void saveStatisticEntry(StatEntryDto entryDto) {
        StatEntry entry = StatsMapper.toStatEntry(entryDto);
        log.info("Сохранение в базу данных записи статистики с параметрами entry={}", entry);
        statsRepository.save(entry);
    }

    @Override
    public List<StatResult> getStatisticByUris(String start, String end, List<String> uris, boolean unique) {
        List<StatResult> result;

        LocalDateTime startDate = DateMapper.toLocalDateTime(start);
        LocalDateTime endDate = DateMapper.toLocalDateTime(end);

        log.info("Запрос к базе данных с параметрами startDate={}, endDate={}, uris={}", startDate, endDate, uris);
        if (unique) {
            if (uris == null) {
                result = statsRepository.findByAllUrisAndUniqueIP(startDate, endDate);
            } else {
                result = statsRepository.findByUrisAndUniqueIP(startDate, endDate, uris);
            }
        } else {
            if (uris == null) {
                result = statsRepository.findByAllUris(startDate, endDate);
            } else {
                result = statsRepository.findByUris(startDate, endDate, uris);
            }
        }
        return result;
    }
}
